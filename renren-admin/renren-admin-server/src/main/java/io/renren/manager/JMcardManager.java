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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@Slf4j
public class JMcardManager {

    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinCardStatusService zinCardStatusService;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JMcardDao jMcardDao;

    // 补充agentId, agentName, merchantName
    public void fillByMerchant(JMcardEntity entity) {
        Long merchantId = entity.getMerchantId();
        SysDeptEntity merchantDept = sysDeptDao.selectById(merchantId);
        Long pid = merchantDept.getPid();
        SysDeptEntity agentDept = sysDeptDao.selectById(pid);
        entity.setAgentId(agentDept.getId());
        entity.setAgentName(agentDept.getName());
        entity.setMerchantName(merchantDept.getName());
    }

    // 保存发卡信息
    public void save(JMcardEntity entity) {
        // 填充ID信息
        Long merchantId = entity.getMerchantId();
        this.fillByMerchant(entity);

        // 费用卡: 什么币种的卡， 就用那个va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(entity.getCurrency())).findFirst().get();
        entity.setPayerid(jVaEntity.getTid());

        // 入库 + 冻结记账
        tx.executeWithoutResult(status -> {
            jMcardDao.insert(entity);
        });
    }

    // 提交通联
    public void submit(JMcardEntity entity) {
        // 向通联发起交易
        TCardSubApplyRequest request = ConvertUtils.sourceToTarget(entity, TCardSubApplyRequest.class);
        request.setMeraplid(entity.getId().toString());
        TCardSubApplyResponse response = zinCardApplyService.cardSubApply(request);

        // 更新applyid
        JMcardEntity update = new JMcardEntity();
        update.setId(entity.getId());
        update.setApplyid(response.getApplyid());
        jMcardDao.updateById(update);
    }

    // 查询通联
    public void query(JMcardEntity jCardEntity) {
        TCardApplyQuery query = new TCardApplyQuery();
        query.setMeraplid(jCardEntity.getId().toString());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        // 准备待更新字段
        JMcardEntity update = new JMcardEntity();
        update.setId(jCardEntity.getId());
        update.setState(response.getState());
        update.setFeecurrency(response.getFeecurrency());
        update.setFee(response.getFee());
        update.setCardno(response.getCardno());

        jMcardDao.updateById(update);
    }

    public void uploadFiles(JMcardEntity cardEntity) {
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

    public void activateCard(JMcardEntity jCardEntity) {
        TCardActivateRequest request = new TCardActivateRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardActivateResponse response = zinCardStatusService.cardActivate(request);
        queryCard(jCardEntity);
    }

    public void lossCard(JMcardEntity jCardEntity) {
        TCardLossRequest request = new TCardLossRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardLossResponse response = zinCardStatusService.cardLoss(request);
        queryCard(jCardEntity);
    }

    public void unlossCard(JMcardEntity jCardEntity) {
        TCardUnlossRequest request = new TCardUnlossRequest();
        request.setCardno(jCardEntity.getCardno());
        zinCardStatusService.cardUnloss(request);
        queryCard(jCardEntity);
    }

    public void freezeCard(JMcardEntity jCardEntity) {
        TCardFreezeRequest request = new TCardFreezeRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardFreezeResponse response = zinCardStatusService.cardFreeze(request);
        queryCard(jCardEntity);
    }

    public void unfreezeCard(JMcardEntity jCardEntity) {
        TCardUnfreezeRequest request = new TCardUnfreezeRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardUnfreezeResponse response = zinCardStatusService.cardUnfreeze(request);
        queryCard(jCardEntity);
    }

    public void cancelCard(JMcardEntity jCardEntity) {
        TCardCancelRequest request = new TCardCancelRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardCancelResponse response = zinCardStatusService.cardCancel(request);
        queryCard(jCardEntity);
    }

    public void uncancelCard(JMcardEntity jCardEntity) {
        TCardUncancelRequest request = new TCardUncancelRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardUncancelResponse response = zinCardStatusService.cardUncancel(request);
        queryCard(jCardEntity);
    }

    public void queryCard(JMcardEntity jCardEntity) {
        TCardStatusQuery request = new TCardStatusQuery();
        request.setCardno(jCardEntity.getCardno());
        TCardStatusResponse response = zinCardStatusService.cardStatusQuery(request);
        JMcardEntity update = new JMcardEntity();
        update.setId(jCardEntity.getId());
        update.setCardState(response.getCardstate());
        jMcardDao.updateById(update);
    }

    // 卡余额
    public void balanceCard(JMcardEntity jCardEntity) {
        TCardBalanceRequest request = new TCardBalanceRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardBalanceResponse response = zinCardMoneyService.balance(request);
        JMcardEntity update = new JMcardEntity();
        update.setId(jCardEntity.getId());
        update.setBalance(response.getBalance());
        jMcardDao.updateById(update);
    }

    //
    public void runList(String id, Consumer<JMcardEntity> consumer) {
        String[] split = id.split(",");
        List<Long> ids = new ArrayList<>();
        for (String str : split) {
            ids.add(Long.parseLong(str));
        }
        List<JMcardEntity> cardList = jMcardDao.selectList(Wrappers.<JMcardEntity>lambdaQuery()
                .in(JMcardEntity::getId, ids)
        );
        for (JMcardEntity entity : cardList) {
            consumer.accept(entity);
        }
    }
}