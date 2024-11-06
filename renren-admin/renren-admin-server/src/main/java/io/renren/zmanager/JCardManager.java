package io.renren.zmanager;


import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.BalanceType;
import io.renren.zbalance.LedgerUtil;
import io.renren.zbalance.ledgers.LedgerOpenCard;
import io.renren.zbalance.ledgers.LedgerPrepaidCharge;
import io.renren.zbalance.ledgers.LedgerPrepaidWithdraw;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZestConstant;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardapply.dto.*;
import io.renren.zin.cardmoney.ZinCardMoneyService;
import io.renren.zin.cardmoney.dto.TCardBalanceRequest;
import io.renren.zin.cardmoney.dto.TCardBalanceResponse;
import io.renren.zin.cardstate.ZinCardStateService;
import io.renren.zin.cardstate.dto.*;
import io.renren.zin.file.ZinFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    private ZinCardStateService zinCardStateService;
    @Resource
    private ZinCardMoneyService zinCardMoneyService;
    @Resource
    private ApiNotify apiNotify;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerOpenCard ledgerOpenCard;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JCardFeeConfigDao jCardFeeConfigDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private LedgerPrepaidWithdraw ledgerPrepaidWithdraw;
    @Resource
    private LedgerPrepaidCharge ledgerPrepaidCharge;
    @Resource
    private JVpaAdjustDao jVpaAdjustDao;

    // 补充agentId, agentName, merchantName
    public void fillBySub(JCardEntity entity) {
        Long subId = entity.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);

        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());

        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());


    }

    public void fillMerchantFee(JCardEntity entity, JMerchantEntity merchant) {
        // 查询开卡费用配置
        List<JCardFeeConfigEntity> cardFeeConfigList = jCardFeeConfigDao.selectList(Wrappers.emptyWrapper());
        BigDecimal fee = null;
        for (JCardFeeConfigEntity cfg : cardFeeConfigList) {
            if (cfg.getCardtype().equals(entity.getCardtype()) &&
                    cfg.getProducttype().equals(entity.getProducttype()) &&
                    cfg.getCurrency().equals(entity.getCurrency())) {
                fee = cfg.getFee();
                break;
            }
        }
        if (fee == null) {
            throw new RenException("can not find open card fee config");
        }

        // 成本
        entity.setFee(fee);

        // 收入
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN)) {
            entity.setMerchantfee(merchant.getVpaMainFee());
        } else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_SHARE)) {
            entity.setMerchantfee(merchant.getVpaShareFee());
        } else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            entity.setMerchantfee(merchant.getVpaPrepaidFee());
        } else if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_REAL)) {
            entity.setMerchantfee(merchant.getVccRealFee());
        } else if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_VIRTUAL)) {
            entity.setMerchantfee(merchant.getVccVirtualFee());
        } else {
            entity.setMerchantfee(BigDecimal.ZERO);
        }
    }

    public void save(JCardEntity entity) {
        // 市场产品 -> 通联产品
        entity.setProducttype(ZinConstant.marketProdcutMap.get(entity.getMarketproduct()));

        // 市场产品 -> 卡类别
        entity.setCardclass(ZinConstant.marketProductClassMap.get(entity.getMarketproduct()));

        // 根据通联产品 --> 币种
        String currency = ZinConstant.productCurrencyMap.get(entity.getProducttype());
        entity.setCurrency(currency);
        entity.setFeecurrency(currency);

        // 卡状态与卡申请状态
        entity.setState(ZinConstant.CARD_APPLY_NEW_DJ);
        entity.setCardState(ZinConstant.CARD_STATE_NEW_DJ);

        // 什么币种的卡， 就用那个va作为payerid
        JVaEntity jVaEntity = jVaDao.selectOne(Wrappers.<JVaEntity>lambdaQuery()
                .eq(JVaEntity::getCurrency, currency)
        );
        entity.setPayerid(jVaEntity.getTid());

        // 将文件上传到通联
        this.uploadFiles(entity);

        // vcc主卡:  不会被客户用
        if (entity.getCardclass().equals(ZinConstant.CC_VCC_MAIN)) {
            log.debug("vcc main card, not used by customer");
            this.saveVccMain(entity);
            return;
        }

        // vcc子卡
        if (entity.getCardclass().equals(ZinConstant.CC_VCC_SUB)) {
            log.debug("vcc sub card");
            this.fillBySub(entity);
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            this.fillMerchantFee(entity, merchant);
            this.saveVccSub(entity);
            return;
        }

        // vpa主卡
        if (entity.getCardclass().equals(ZinConstant.CC_VPA_MAIN)) {
            log.debug("vpa main card");
            if (entity.getProcurecontent() == null || entity.getPayeeaccount() == null || entity.getAgmfid() == null) {
                throw new RenException("交易对手, 采购合同, 采购内容必填");
            }
            this.fillBySub(entity);
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            entity.setCusid(merchant.getCusid());
            this.saveVpaMain(entity);
            return;
        }

        // vpa子卡
        if (entity.getCardclass().equals(ZinConstant.CC_VPA_SUB)) {
            log.debug("vpa sub card");
            this.fillBySub(entity);
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            this.fillMerchantFee(entity, merchant);
            this.saveVpaSub(entity);
            return;
        }
        log.error("unknown card class");

        throw new RenException("unknown cardclass");
    }

    // vcc主卡: 平台系统内部用
    public void saveVccMain(JCardEntity entity) {
        entity.setAgentId(0L);
        entity.setAgentName("void");
        entity.setMerchantId(0L);
        entity.setMerchantName("void");
        entity.setSubId(0L);
        entity.setSubName("void");
        jCardDao.insert(entity);
    }

    // vcc子卡
    public void saveVccSub(JCardEntity entity) {
        // 查询子商户va余额
        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (subVaAccount == null) {
            log.error("无法找到子va: {}", entity.getSubId());
            throw new RenException("内部错误, 请联系管理员");
        }
        log.debug("subVaAccount:{}", subVaAccount);

        BigDecimal balance = subVaAccount.getBalance();
        if (balance.compareTo(entity.getMerchantfee()) < 0) {
            throw new RenException(String.format("余额不足, 余额:%s, 开卡费用:%s", subVaAccount.getBalance(), entity.getMerchantfee()));
        }

        // 查询商户的开通的主卡, 将子卡挂到某个主卡下
        JCardEntity jCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCurrency, entity.getCurrency())
                .eq(JCardEntity::getCardtype, entity.getCardtype())
                .eq(JCardEntity::getProducttype, entity.getProducttype())
                .eq(JCardEntity::getCardclass, ZinConstant.CC_VCC_MAIN)
                .eq(JCardEntity::getCardState, ZinConstant.CARD_STATE_SUCCESS)
        );
        if (jCardEntity == null) {
            throw new RenException("请先开通主卡-" + entity.getCurrency() + "-" + entity.getCardtype() + "-" + entity.getProducttype());
        }
        entity.setMaincardno(jCardEntity.getCardno());

        // 如果子卡是主体是合作企业， 则通联接口要求必须填cusid
        if (entity.getBelongtype().equals(ZinConstant.BELONG_TYPE_COOP)) {
            JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
            entity.setCusid(merchant.getCusid());
        }

        // 入库 + 冻结记账
        tx.executeWithoutResult(status -> {
            jCardDao.insert(entity);
            ledgerOpenCard.ledgeOpenCardFreeze(entity);
        });
    }

    /**
     * 创建预付费卡主账户
     */
    private void newPrepaidBalance(JCardEntity entity) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(entity.getId());
        jBalanceEntity.setOwnerName(entity.getCardno());
        jBalanceEntity.setOwnerType(ZestConstant.USER_TYPE_PREPAID);
        jBalanceEntity.setBalanceType(BalanceType.getPrepaidAccount(entity.getCurrency()));
        jBalanceEntity.setCurrency(entity.getCurrency());
        jBalanceDao.insert(jBalanceEntity);
    }

    // 共享主卡 | 预付费主卡
    public void saveVpaMain(JCardEntity entity) {
        tx.executeWithoutResult(status -> {
            if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
                newPrepaidBalance(entity);
            }
            jCardDao.insert(entity);
        });
    }

    // 共享子卡: 应该不在这里创建
    public void saveVpaSub(JCardEntity entity) {
    }

    // 提交通联
    public void submit(JCardEntity entity) {
        String applyid = null;
        if (entity.getCardclass().equals(ZinConstant.CC_VPA_MAIN) || entity.getCardclass().equals(ZinConstant.CC_VCC_MAIN)) {
            // 主卡申请
            TCardMainApplyRequest request = ConvertUtils.sourceToTarget(entity, TCardMainApplyRequest.class);
            request.setMeraplid(entity.getId().toString());  // 需要换成我们的ID!!!
            TCardMainApplyResponse response = zinCardApplyService.cardMainApply(request);
            applyid = response.getApplyid();
        } else {
            // vpa子卡申请
            TCardSubApplyRequest request = ConvertUtils.sourceToTarget(entity, TCardSubApplyRequest.class);
            request.setMeraplid(entity.getId().toString());  // 需要换成我们的ID!!!
            TCardSubApplyResponse response = zinCardApplyService.cardSubApply(request);
            applyid = response.getApplyid();
        }

        // 更新applyid
        JCardEntity update = new JCardEntity();
        update.setId(entity.getId());
        update.setApplyid(applyid);
        jCardDao.updateById(update);

        // 带入下
        entity.setApplyid(applyid);
    }

    // 查询发卡状态
    public void query(JCardEntity jCardEntity, boolean notify) {

        TCardApplyQuery query = new TCardApplyQuery();
        query.setApplyid(jCardEntity.getApplyid());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        // 从非失败  -> 失败,  处理退款
        String prevState = jCardEntity.getState();
        String nextState = response.getState();

        // 更新状态， 以及发卡成本， 卡号等
        LambdaUpdateWrapper<JCardEntity> updateWrapper = Wrappers.<JCardEntity>lambdaUpdate()
                .eq(JCardEntity::getId, jCardEntity.getId())
                .eq(JCardEntity::getState, prevState)
                .set(JCardEntity::getState, nextState)
                .set(response.getFeecurrency() != null, JCardEntity::getFeecurrency, response.getFeecurrency())
                .set(response.getFee() != null, JCardEntity::getFee, response.getFee())
                .set(response.getCardno() != null, JCardEntity::getCardno, response.getCardno());

        // 不成功 -> 成功
        if (!ZinConstant.isCardApplySuccess(prevState) && ZinConstant.isCardApplySuccess(nextState)) {

            // 调用下通联获取cvv2 + expiredate
            TCardPayInfoRequest req = new TCardPayInfoRequest();
            req.setCardno(response.getCardno());
            TCardPayInfoResponse resp = zinCardApplyService.cardPayInfo(req);
            String cvv = resp.getCvv();
            String expiredate = resp.getExpiredate();

            // 有效期解密保存
            String decryptedExpiredate = CommonUtils.decryptSensitiveString(expiredate, zestConfig.getAccessConfig().getSensitiveKey(), "UTF-8");

            updateWrapper.set(JCardEntity::getCvv, cvv)
                    .set(JCardEntity::getExpiredate, decryptedExpiredate);

            // vcc子卡有收费处理
            if (jCardEntity.getCardclass().equals(ZinConstant.CC_VCC_SUB)) {
                tx.executeWithoutResult(status -> {
                    jCardDao.update(null, updateWrapper);
                    ledgerOpenCard.ledgeOpenCard(jCardEntity);
                });
            } else {
                // 其他卡没有收费处理
                jCardDao.update(null, updateWrapper);
            }

            // 是否需要通知api商户
            if (jCardEntity.getApi().equals(1) && notify) {
                // 发卡状态更新
                JCardEntity entity = jCardDao.selectById(jCardEntity.getId());
                JMerchantEntity merchant = jMerchantDao.selectById(jCardEntity.getMerchantId());
                apiNotify.cardNewNotify(entity, merchant);
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
                ledgerOpenCard.ledgeOpenCardUnFreeze(jCardEntity);
            });
            // 通知商户
            if (jCardEntity.getApi().equals(1) && notify) {
                // 发卡状态更新
                JCardEntity entity = jCardDao.selectById(jCardEntity.getId());
                JMerchantEntity merchant = jMerchantDao.selectById(jCardEntity.getMerchantId());
                apiNotify.cardChangeNotify(entity, merchant);
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
        String agmfid = cardEntity.getAgmfid();

        List<String> fids = new ArrayList<>();
        if (photofront != null) {
            fids.add(photofront);
        }
        if (photoback != null) {
            fids.add(photoback);
        }
        if (photofront2 != null) {
            fids.add(photofront2);
        }
        if (photoback2 != null) {
            fids.add(photoback2);
        }
        if (agmfid != null) {
            fids.add(agmfid);
        }
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
        TCardActivateResponse response = zinCardStateService.cardActivate(request);
        queryCard(jCardEntity);
    }

    public void lossCard(JCardEntity jCardEntity) {
        TCardLossRequest request = new TCardLossRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardLossResponse response = zinCardStateService.cardLoss(request);
        queryCard(jCardEntity);
    }

    public void unlossCard(JCardEntity jCardEntity) {
        TCardUnlossRequest request = new TCardUnlossRequest();
        request.setCardno(jCardEntity.getCardno());
        zinCardStateService.cardUnloss(request);
        queryCard(jCardEntity);
    }

    public void freezeCard(JCardEntity jCardEntity) {
        TCardFreezeRequest request = new TCardFreezeRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardFreezeResponse response = zinCardStateService.cardFreeze(request);
        queryCard(jCardEntity);
    }

    public void unfreezeCard(JCardEntity jCardEntity) {
        TCardUnfreezeRequest request = new TCardUnfreezeRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardUnfreezeResponse response = zinCardStateService.cardUnfreeze(request);
        queryCard(jCardEntity);
    }

    public void cancelCard(JCardEntity jCardEntity) {
        TCardCancelRequest request = new TCardCancelRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardCancelResponse response = zinCardStateService.cardCancel(request);
        queryCard(jCardEntity);
    }

    public void uncancelCard(JCardEntity jCardEntity) {
        TCardUncancelRequest request = new TCardUncancelRequest();
        request.setCardno(jCardEntity.getCardno());
        TCardUncancelResponse response = zinCardStateService.cardUncancel(request);
        queryCard(jCardEntity);
    }

    // 查询卡面状态
    public void queryCard(JCardEntity jCardEntity) {

        TCardStatusQuery request = new TCardStatusQuery();
        request.setCardno(jCardEntity.getCardno());
        TCardStatusResponse response = zinCardStateService.cardStatusQuery(request);
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setCardState(response.getCardstate());
        jCardEntity.setCardState(response.getCardstate());

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
        jCardEntity.setBalance(response.getBalance());
        jCardDao.updateById(update);

    }

    // 查询某张卡的余额
    public BigDecimal getBalance(String cardno) {
        TCardBalanceRequest request = new TCardBalanceRequest();
        request.setCardno(cardno);
        TCardBalanceResponse response = zinCardMoneyService.balance(request);
        return response.getBalance();
    }

    // 并行查询所有卡的余额
    public Map<String, BigDecimal> batchBalance(List<String> cardnoList) {
        List<CompletableFuture<Pair<String, BigDecimal>>> futures = new ArrayList<>();
        for (String cardno : cardnoList) {
            CompletableFuture<Pair<String, BigDecimal>> f = CompletableFuture.<Pair<String, BigDecimal>>supplyAsync(() -> {
                BigDecimal balance = getBalance(cardno);
                return Pair.of(cardno, balance);
            });
            futures.add(f);
        }
        Map<String, BigDecimal> map = new HashMap<>();
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[cardnoList.size()])).get();
            for (CompletableFuture<Pair<String, BigDecimal>> future : futures) {
                Pair<String, BigDecimal> pair = future.get();
                map.put(pair.getKey(), pair.getValue());
            }
            return map;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RenException("查询余额失败");
        }
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

    // 更新卡状态以及卡的余额
    public void updateCard(Long id) {
        JCardEntity cardEntity = jCardDao.selectById(id);

        // 查寻卡状态
        TCardStatusQuery request = new TCardStatusQuery();
        request.setCardno(cardEntity.getCardno());
        TCardStatusResponse response = zinCardStateService.cardStatusQuery(request);

        // 查询余额
        TCardBalanceRequest balanceRequest = new TCardBalanceRequest();
        balanceRequest.setCardno(cardEntity.getCardno());
        TCardBalanceResponse balanceResponse = zinCardMoneyService.balance(balanceRequest);

        // 更新
        JCardEntity update = new JCardEntity();
        update.setId(cardEntity.getId());
        update.setCardState(response.getCardstate());
        update.setBalance(balanceResponse.getBalance());
        jCardDao.updateById(update);
    }

    // 预付费卡-充值
    public void prepaidCharge(Long id, BigDecimal adjustAmount) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        BigDecimal oldAuth = cardEntity.getAuthmaxamount();
        BigDecimal newAuth = cardEntity.getAuthmaxamount().add(adjustAmount);
        JVpaAdjustEntity adjustEntity = ConvertUtils.sourceToTarget(cardEntity, JVpaAdjustEntity.class);
        adjustEntity.setAdjustAmount(adjustAmount);
        adjustEntity.setOldQuote(oldAuth);
        adjustEntity.setNewQuote(newAuth);
        adjustEntity.setState(ZinConstant.VPA_ADJUST_UNKNOWN);
        adjustEntity.setMaincardno(cardEntity.getMaincardno());
        jVpaAdjustDao.insert(adjustEntity);

        // 冻结
        tx.executeWithoutResult(st -> {
            ledgerPrepaidCharge.ledgePrepaidChargeFreeze(adjustEntity);
        });

        // 发起变更
        TCardUpdateScene request = new TCardUpdateScene();
        try {
            TCardUpdateSceneResponse response = zinCardApplyService.cardUpdateScene(request);
        } catch (Exception ex) {

        }

    }

    // 预付费卡-提现
    public void prepaidWithdraw(Long id, BigDecimal adjustAmount) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        BigDecimal oldAuth = cardEntity.getAuthmaxamount();
        BigDecimal newAuth = cardEntity.getAuthmaxamount().subtract(adjustAmount);
        JVpaAdjustEntity adjustEntity = ConvertUtils.sourceToTarget(cardEntity, JVpaAdjustEntity.class);
        adjustEntity.setAdjustAmount(adjustAmount);
        adjustEntity.setOldQuote(oldAuth);
        adjustEntity.setNewQuote(newAuth);
        adjustEntity.setState(ZinConstant.VPA_ADJUST_UNKNOWN);
        adjustEntity.setMaincardno(cardEntity.getMaincardno());
        jVpaAdjustDao.insert(adjustEntity);


        tx.executeWithoutResult(st -> {
            ledgerPrepaidCharge.ledgePrepaidCharge(adjustEntity);
        });
    }

    // 共享子卡设置额度
    public void setQuota(Long id, BigDecimal amount) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        BigDecimal oldAuth = cardEntity.getAuthmaxamount();
        BigDecimal newAuth = amount;
        BigDecimal adjustAmount = newAuth.subtract(oldAuth);

        JVpaAdjustEntity adjustEntity = ConvertUtils.sourceToTarget(cardEntity, JVpaAdjustEntity.class);
        adjustEntity.setAdjustAmount(adjustAmount);
        adjustEntity.setOldQuote(oldAuth);
        adjustEntity.setNewQuote(newAuth);
        adjustEntity.setState(ZinConstant.VPA_ADJUST_UNKNOWN);
        adjustEntity.setMaincardno(cardEntity.getMaincardno());
        jVpaAdjustDao.insert(adjustEntity);

        TCardUpdateScene request = new TCardUpdateScene();
        try {
            TCardUpdateSceneResponse response = zinCardApplyService.cardUpdateScene(request);
            // 调整成功
            tx.executeWithoutResult(st -> {
                jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                        .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                        .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                        .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_SUCCESS)
                );
                // 更新卡的当前额度
                jCardDao.update(null, Wrappers.<JCardEntity>lambdaUpdate()
                        .eq(JCardEntity::getId, cardEntity.getId())
                        .set(JCardEntity::getAuthmaxamount, newAuth)
                );
            });
        } catch (Exception ex) {
            // 查询下， 如果明确失败
            jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                    .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                    .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                    .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_FAIL)
            );
            throw new RenException("调整失败");
        }
    }
}
