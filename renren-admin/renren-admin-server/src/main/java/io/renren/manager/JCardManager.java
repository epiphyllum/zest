package io.renren.manager;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.entity.*;
import io.renren.zapi.notifyevent.CardApplyNotifyEvent;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import io.renren.zin.config.CardProductConfig;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinConstant;
import io.renren.zin.service.cardapply.ZinCardApplyService;
import io.renren.zin.service.cardapply.dto.TCardApplyQuery;
import io.renren.zin.service.cardapply.dto.TCardApplyResponse;
import io.renren.zin.service.cardapply.dto.TCardSubApplyRequest;
import io.renren.zin.service.cardapply.dto.TCardSubApplyResponse;
import io.renren.zin.service.file.ZinFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JCardManager {

    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ApplicationEventPublisher publisher;

    @Resource
    private TransactionTemplate tx;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JMcardDao jMcardDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private Ledger ledger;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private ZestConfig zestConfig;


    // 补充agentId, agentName, merchantName
    public void fillByMerchant(JCardEntity entity) {
        Long merchantId = entity.getMerchantId();
        SysDeptEntity merchantDept = sysDeptDao.selectById(merchantId);
        Long pid = merchantDept.getPid();
        SysDeptEntity agentDept = sysDeptDao.selectById(pid);
        SysDeptEntity subDept = sysDeptDao.selectById(entity.getSubId());
        entity.setAgentId(agentDept.getId());
        entity.setAgentName(agentDept.getName());
        entity.setMerchantName(merchantDept.getName());
        entity.setSubName(subDept.getName());
    }

    // 保存发卡信息
    public void save(JCardEntity entity) {
        // 查询开卡费用配置
        CardProductConfig config = zestConfig.getCardProductConfig(entity.getProducttype(), entity.getCurrency(), entity.getCardtype());

        // 查询子商户va余额
        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (subVaAccount.getBalance().compareTo(config.getFee()) == -1) {
            throw new RenException("余额不足");
        }

        // 填充ID信息
        Long merchantId = entity.getMerchantId();
        this.fillByMerchant(entity);

        // 费用卡:  什么币种的卡， 就用那个va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayerid(jVaEntity.getTid());

        // 查询商户的开通的主卡, 将子卡挂到某个主卡下
        JMcardEntity jMcardEntity = jMcardDao.selectOne(Wrappers.<JMcardEntity>lambdaQuery()
                .eq(JMcardEntity::getMerchantId, merchantId)
                .eq(JMcardEntity::getCurrency, entity.getCurrency())
                .eq(JMcardEntity::getCardtype, entity.getCardtype())
                .eq(JMcardEntity::getProducttype, entity.getProducttype())
                .eq(JMcardEntity::getState, "04")
        );
        if (jMcardEntity == null) {
            throw new RenException("请先给商户开通主卡-" + entity.getCurrency() + "-" + entity.getCardtype() + "-" + entity.getProducttype());
        }
        entity.setMaincardno(jMcardEntity.getCardno());

        // 如果子卡是主体是合作企业， 则通联接口要求必须填cusid
        if (entity.getBelongtype().equals("2")) {
            JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
            entity.setCusid(merchant.getCusid());
        }

        // 发卡收费
        entity.setMerchantFee(config.getFee());

        // 将文件上传到通联
        this.uploadFiles(entity);

        // 入库 + 冻结记账
        tx.executeWithoutResult(status -> {
            jCardDao.insert(entity);
            ledger.ledgeOpenCardFreeze(entity);
        });
    }

    // 提交通联
    public void submit(JCardEntity entity) {
        // 向通联发起交易
        TCardSubApplyRequest request = ConvertUtils.sourceToTarget(entity, TCardSubApplyRequest.class);
        request.setMeraplid(entity.getId().toString());
        TCardSubApplyResponse response = zinCardApplyService.cardSubApply(request);

        // 更新applyid
        JCardEntity update = new JCardEntity();
        update.setId(entity.getId());
        update.setApplyid(response.getApplyid());
        jCardDao.updateById(update);
    }

    // 查询通联
    public void query(JCardEntity jCardEntity) {
        TCardApplyQuery query = new TCardApplyQuery();
        query.setMeraplid(jCardEntity.getId().toString());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        // 准备待更新字段
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setState(response.getState());
        update.setFeecurrency(response.getFeecurrency());
        update.setFee(response.getFee());
        update.setCardno(response.getCardno());

        // 开卡成功了: 确认记账
        if (response.getState().equals(ZinConstant.CARD_APPLY_SUCCESS)) {
            tx.executeWithoutResult(status -> {
                jCardDao.updateById(update);
                ledger.ledgeOpenCard(jCardEntity);
            });
            if (jCardEntity.getApi().equals(1)) {
                publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
            }
            return;
        }

        // 从非失败  -> 失败,  处理退款
        String prevState = jCardEntity.getState();
        String nextState = response.getState();
        if (!(prevState.equals(ZinConstant.CARD_APPLY_VERIFY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_CLOSE)
        ) && (nextState.equals(ZinConstant.CARD_APPLY_VERIFY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_CLOSE))
        ) {
            // 解冻释放
            tx.executeWithoutResult(status -> {
                jCardDao.updateById(update);
                ledger.ledgeOpenCardUnFreeze(jCardEntity);
            });
            // 通知商户
            if (jCardEntity.getApi().equals(1)) {
                publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
            }
        } else {
            jCardDao.updateById(update);
        }
    }

    public void uploadFiles(JCardEntity cardEntity) {
        // 拿到所有文件fid
        String photofront = cardEntity.getPhotofront();
        String photoback = cardEntity.getPhotoback();
        String photofront2 = cardEntity.getPhotofront2();
        String photoback2 = cardEntity.getPhotoback2();

        List<String> fids = List.of(photofront, photoback, photofront2, photoback2);
        Map<String, CompletableFuture<String>> jobs = new HashMap<>();
        for (String fid : fids) {
            if (StringUtils.isBlank(fid)) {
                continue;
            }
            jobs.put(fid, CompletableFuture.supplyAsync(() -> {
                return zinFileService.upload(fid);
            }));
        }
        jobs.forEach((j, f) -> {
            log.info("wait {}...", j);
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RenException("can not upload file:" + j);
            }
        });
        log.info("文件上传完毕, 开始请求创建商户...");
    }
}
