package io.renren.manager;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zapi.notifyevent.CardApplyNotifyEvent;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinConstant;
import io.renren.zin.service.cardapply.ZinCardApplyService;
import io.renren.zin.service.cardapply.dto.*;
import io.renren.zin.service.cardmoney.ZinCardMoneyService;
import io.renren.zin.service.cardmoney.dto.TCardBalanceRequest;
import io.renren.zin.service.cardmoney.dto.TCardBalanceResponse;
import io.renren.zin.service.cardstatus.ZinCardStatusService;
import io.renren.zin.service.cardstatus.dto.*;
import io.renren.zin.service.file.ZinFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@Slf4j
public class JCardManager {

    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStatusService zinCardStatusService;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;
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
    @Resource
    private JCardFeeConfigDao jCardFeeConfigDao;

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
        List<JCardFeeConfigEntity> cardFeeConfigList = jCardFeeConfigDao.selectList(Wrappers.emptyWrapper());
        BigDecimal fee = null;
        for (JCardFeeConfigEntity cfg : cardFeeConfigList) {
            if(cfg.getCardtype().equals(entity.getCardtype()) &&
                    cfg.getProducttype().equals(entity.getProducttype()) &&
                    cfg.getCurrency().equals(entity.getCurrency()) ) {
                fee = cfg.getFee();
                break;
            }
        }
        if (fee == null) {
            throw new RenException("can not find open card fee config");
        }

        // 查询子商户va余额
        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (subVaAccount.getBalance().compareTo(fee) == -1) {
            throw new RenException("余额不足");
        }

        // 填充ID信息
        Long merchantId = entity.getMerchantId();
        this.fillByMerchant(entity);

        entity.setState(ZinConstant.CARD_APPLY_NEW_DJ);
        entity.setCardState(ZinConstant.CARD_STATE_NEW_DJ);

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
                .eq(JMcardEntity::getCardState, ZinConstant.CARD_STATE_SUCCESS)
        );
        if (jMcardEntity == null) {
            throw new RenException("请先给商户开通主卡-" + entity.getCurrency() + "-" + entity.getCardtype() + "-" + entity.getProducttype());
        }
        entity.setMaincardno(jMcardEntity.getCardno());

        // 如果子卡是主体是合作企业， 则通联接口要求必须填cusid
        if (entity.getBelongtype().equals(ZinConstant.BELONG_TYPE_COOP)) {
            JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
            entity.setCusid(merchant.getCusid());
        }

        // 发卡收费
        entity.setMerchantFee(fee);

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

    // 查询发卡状态
    public void query(JCardEntity jCardEntity) {
        TCardApplyQuery query = new TCardApplyQuery();
        query.setMeraplid(jCardEntity.getId().toString());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        // 从非失败  -> 失败,  处理退款
        String prevState = jCardEntity.getState();
        String nextState = response.getState();

        LambdaUpdateWrapper<JCardEntity> updateWrapper = Wrappers.<JCardEntity>lambdaUpdate()
                .eq(JCardEntity::getId, jCardEntity.getId())
                .eq(JCardEntity::getState, prevState)
                .set(JCardEntity::getState, nextState)
                .set(JCardEntity::getFeecurrency, response.getFeecurrency())
                .set(JCardEntity::getFee, response.getFee())
                .set(JCardEntity::getCardno, response.getCardno());

        // 不成功 -> 成功
        if (!ZinConstant.isCardApplySuccess(prevState) && ZinConstant.isCardApplySuccess(nextState)) {

            // 调用下通联获取cvv2 + expiredate
            TCardPayInfoRequest req = new TCardPayInfoRequest();
            req.setCardno(response.getCardno());
            TCardPayInfoResponse resp = zinCardApplyService.cardPayInfo(req);
            String cvv = zestConfig.decryptSensitive(resp.getCvv());
            String expiredate = zestConfig.decryptSensitive(resp.getExpiredate());

            updateWrapper.set(JCardEntity::getCvv, cvv).set(JCardEntity::getExpiredate, expiredate);
            tx.executeWithoutResult(status -> {
                jCardDao.update(null, updateWrapper);
                ledger.ledgeOpenCard(jCardEntity);
            });

            if (jCardEntity.getApi().equals(1)) {
                publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
            }

            // 发卡成功, 查询更新状态
            jCardEntity.setCardno(response.getCardno());
            this.queryCard(jCardEntity);

            return;
        }

        // 非失败 -> 失败
        if (!ZinConstant.isCardApplyFail(prevState) && ZinConstant.isCardApplyFail(nextState)) {
            // 解冻释放
            tx.executeWithoutResult(status -> {
                jCardDao.update(null, updateWrapper);
                ledger.ledgeOpenCardUnFreeze(jCardEntity);
            });
            // 通知商户
            if (jCardEntity.getApi().equals(1)) {
                publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
            }
        }
        // 其他情况
        jCardDao.update(null, updateWrapper);
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

    public void activateCard(JCardEntity jCardEntity) {
        TCardActivateRequest request = new TCardActivateRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardActivateResponse response = zinCardStatusService.cardActivate(request);
        queryCard(jCardEntity);
    }

    public void lossCard(JCardEntity jCardEntity) {
        TCardLossRequest request = new TCardLossRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardLossResponse response = zinCardStatusService.cardLoss(request);
        queryCard(jCardEntity);
    }

    public void unlossCard(JCardEntity jCardEntity) {
        TCardUnlossRequest request = new TCardUnlossRequest();
        request.setCardno(jCardEntity.getCardno());
        zinCardStatusService.cardUnloss(request);
        queryCard(jCardEntity);
    }

    public void freezeCard(JCardEntity jCardEntity) {
        TCardFreezeRequest request = new TCardFreezeRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardFreezeResponse response = zinCardStatusService.cardFreeze(request);
        queryCard(jCardEntity);
    }

    public void unfreezeCard(JCardEntity jCardEntity) {
        TCardUnfreezeRequest request = new TCardUnfreezeRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardUnfreezeResponse response = zinCardStatusService.cardUnfreeze(request);
        queryCard(jCardEntity);
    }

    public void cancelCard(JCardEntity jCardEntity) {
        TCardCancelRequest request = new TCardCancelRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardCancelResponse response = zinCardStatusService.cardCancel(request);
        queryCard(jCardEntity);
    }

    public void uncancelCard(JCardEntity jCardEntity) {
        TCardUncancelRequest request = new TCardUncancelRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardUncancelResponse response = zinCardStatusService.cardUncancel(request);
        queryCard(jCardEntity);
    }

    // 查询卡面状态
    public void queryCard(JCardEntity jCardEntity) {
        TCardStatusQuery request = new TCardStatusQuery();
        request.setCardno(jCardEntity.getCardno());
        TCardStatusResponse response = zinCardStatusService.cardStatusQuery(request);
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setCardState(response.getCardstate());
        jCardDao.updateById(update);
    }

    // 卡余额
    public void balanceCard(JCardEntity jCardEntity) {
        TCardBalanceRequest request = new TCardBalanceRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardBalanceResponse response = zinCardMoneyService.balance(request);
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setBalance(response.getBalance());
        jCardDao.updateById(update);
    }

    // 批量操作卡状态变更
    public void runList(String id, Consumer<JCardEntity> consumer) {
        String[] split = id.split(",");
        List<Long> ids = new ArrayList<>();
        for (String str : split) {
            ids.add(Long.parseLong(str));
        }
        List<JCardEntity> cardList = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .in(JCardEntity::getId, ids)
        );
        for (JCardEntity entity : cardList) {
            consumer.accept(entity);
        }
    }
}
