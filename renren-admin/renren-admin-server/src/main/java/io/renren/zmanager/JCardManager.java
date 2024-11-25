package io.renren.zmanager;


import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@Slf4j
public class JCardManager {
    @Resource
    private JCommon jCommon;
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
    private JSubDao jSubDao;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private LedgerPrepaidWithdraw ledgerPrepaidWithdraw;
    @Resource
    private LedgerPrepaidCharge ledgerPrepaidCharge;
    @Resource
    private JVpaAdjustDao jVpaAdjustDao;
    @Resource
    private JConfigDao jConfigDao;

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
        JFeeConfigEntity feeConfig = jCommon.getFeeConfig(merchant.getId(), entity.getMarketproduct());
        entity.setFee(feeConfig.getCostCardFee());
        entity.setMerchantfee(feeConfig.getCardFee());
    }

    public void save(JCardEntity entity) {
        Map<String, String> productMap = ZinConstant.marketProdcutMap.get(entity.getCurrency());
        if (productMap == null) {
            throw new RenException("币种不合法:" + entity.getCurrency());
        }
        String baseProduct = productMap.get(entity.getMarketproduct());
        if (baseProduct == null) {
            throw new RenException("产品类型不合法:" + entity.getMarketproduct());
        }

        // 市场产品 -> 通联产品
        entity.setProducttype(baseProduct);

        // 开卡费用币种
        entity.setFeecurrency(entity.getCurrency());

        // 卡状态与卡申请状态
        entity.setState(ZinConstant.CARD_APPLY_NEW_DJ);
        entity.setCardstate(ZinConstant.CARD_STATE_NEW_DJ);

        // 卡类型
        entity.setCardtype(ZinConstant.cardTypeMap.get(entity.getProducttype()));

        // 什么币种的卡， 就用哪个va作为payerid: 通联需要扣费
        JVaEntity jVaEntity = jVaDao.selectOne(Wrappers.<JVaEntity>lambdaQuery().eq(JVaEntity::getCurrency, entity.getCurrency()));
        entity.setPayerid(jVaEntity.getTid());

        // 虚拟主卡, 实体主卡不对外
        if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_MAIN_REAL) ||
                entity.getMarketproduct().equals(ZinConstant.MP_VCC_MAIN_VIRTUAL)
        ) {
            this.saveVccMain(entity);
            return;
        }

        // 填充子相关方ID
        this.fillBySub(entity);
        // 查询商户
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        // 填充开卡成本与收费
        fillMerchantFee(entity, merchant);

        // 共享主卡, 预付费主卡, 需要提交额外字段
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN) ||
                entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)
        ) {
            if (entity.getProcurecontent() == null || entity.getPayeeaccount() == null || entity.getAgmfid() == null) {
                throw new RenException("交易对手, 采购合同, 采购内容必填");
            }
            entity.setCusid(merchant.getCusid());
            this.saveVpaMain(entity);
            return;
        }

        // 虚拟子卡， 实体子卡
        if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_REAL) ||
                entity.getMarketproduct().equals(ZinConstant.MP_VCC_VIRTUAL)
        ) {
            this.saveVccSub(entity);
            return;
        }

        // 共享子卡， 预付费子卡不在这里开通
        throw new RenException("产品类型错误:" + entity.getMarketproduct());
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
        // 子卡的belongtype必须是合作企业
        entity.setBelongtype(ZinConstant.BELONG_TYPE_COOP);
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        entity.setCusid(merchant.getCusid());

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
        JConfigEntity jConfigEntity = jConfigDao.selectOne(Wrappers.emptyWrapper());
        if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_REAL)) {
            entity.setMaincardno(jConfigEntity.getVccMainReal());
        } else if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_VIRTUAL)) {
            entity.setMaincardno(jConfigEntity.getVccMainVirtual());
        } else {
            throw new RenException("请求非法");
        }

        // 入库 + 冻结记账
        tx.executeWithoutResult(status -> {
            jCardDao.insert(entity);
            ledgerOpenCard.ledgeOpenCardFreeze(entity);
        });
    }

    // 共享主卡 | 预付费主卡
    public void saveVpaMain(JCardEntity entity) {
        tx.executeWithoutResult(status -> {
            jCardDao.insert(entity);
            ledgerOpenCard.ledgeOpenCardFreeze(entity);
        });
    }

    /**
     * 创建预付费卡额度主账户
     */
    private void newPrepaidQuotaBalance(JCardEntity entity) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(entity.getId());
        jBalanceEntity.setOwnerName(entity.getCardno());
        jBalanceEntity.setOwnerType(ZestConstant.USER_TYPE_PREPAID);
        jBalanceEntity.setBalanceType(BalanceType.getPrepaidQuotaAccount(entity.getCurrency()));
        jBalanceEntity.setCurrency(entity.getCurrency());
        jBalanceDao.insert(jBalanceEntity);
    }

    /**
     * 创建预付费卡发卡额
     */
    private void newPrepaidSumBalance(JCardEntity entity) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(entity.getId());
        jBalanceEntity.setOwnerName(entity.getCardno());
        jBalanceEntity.setOwnerType(ZestConstant.USER_TYPE_PREPAID);
        jBalanceEntity.setBalanceType(BalanceType.getPrepaidSumAccount(entity.getCurrency()));
        jBalanceEntity.setCurrency(entity.getCurrency());
        jBalanceDao.insert(jBalanceEntity);
    }

    // 提交通联
    public void submit(JCardEntity entity) {
        String applyid = null;
        if (entity.getMarketproduct().equals(ZinConstant.MP_VCC_MAIN_VIRTUAL) ||
                entity.getMarketproduct().equals(ZinConstant.MP_VCC_MAIN_REAL) ||
                entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN) ||
                entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)
        ) {
            // 主卡申请
            TCardMainApplyRequest request = ConvertUtils.sourceToTarget(entity, TCardMainApplyRequest.class);
            request.setMeraplid(entity.getTxnid());  // 需要换成我们的ID!!!
            TCardMainApplyResponse response = zinCardApplyService.cardMainApply(request);
            applyid = response.getApplyid();
        } else {
            // 子卡申请
            TCardSubApplyRequest request = ConvertUtils.sourceToTarget(entity, TCardSubApplyRequest.class);
            request.setMeraplid(entity.getTxnid());  // 需要换成我们的ID!!!
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
        query.setMeraplid(jCardEntity.getTxnid());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        //
        String prevState = jCardEntity.getState();
        String nextState = response.getState();

        // 更新状态， 以及发卡成本， 卡号等
        LambdaUpdateWrapper<JCardEntity> updateWrapper = Wrappers.<JCardEntity>lambdaUpdate()
                .eq(JCardEntity::getId, jCardEntity.getId())
                .eq(JCardEntity::getState, prevState)
                .set(JCardEntity::getState, nextState)
                .set(response.getFeecurrency() != null, JCardEntity::getFeecurrency, response.getFeecurrency())
                .set(response.getFee() != null, JCardEntity::getFee, response.getFee())
                .set(response.getCardno() != null, JCardEntity::getCardno, response.getCardno())
                .set(response.getState() != null, JCardEntity::getStateexplain, response.getStateexplain());

        // 不成功 -> 成功
        if (!ZinConstant.isCardApplySuccess(prevState) && ZinConstant.isCardApplySuccess(nextState)) {

            // 完成时间
            updateWrapper.set(JCardEntity::getStatDate, new Date());

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
            // 备用
            jCardEntity.setCardno(response.getCardno());
            jCardEntity.setFee(response.getFee());  // 通联实际费用
            jCardEntity.setFeecurrency(response.getFeecurrency());
            JMerchantEntity merchant = jMerchantDao.selectById(jCardEntity.getMerchantId());
            // vcc虚拟卡， vcc实体卡, 共享主卡, 预付费主卡, 四种卡有收费有收费处理
            String mpType = jCardEntity.getMarketproduct();
            if (mpType.equals(ZinConstant.MP_VCC_VIRTUAL) ||
                    mpType.equals(ZinConstant.MP_VCC_REAL) ||
                    mpType.equals(ZinConstant.MP_VPA_MAIN) ||
                    mpType.equals(ZinConstant.MP_VPA_MAIN_PREPAID)
            ) {
                tx.executeWithoutResult(status -> {
                    // 预付费主卡, 需要做剩余额度账户管理, 建立预付费主卡, 剩余额度管理账户
                    if (jCardEntity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
                        this.newPrepaidQuotaBalance(jCardEntity);
                        this.newPrepaidSumBalance(jCardEntity);
                    }
                    jCardDao.update(null, updateWrapper);
                    ledgerOpenCard.ledgeOpenCard(jCardEntity);
                });
            } else {
                // 其他卡没有收费处理
                jCardDao.update(null, updateWrapper);
            }
            // 发卡成功, 查询更新状态
            jCardEntity.setCardno(response.getCardno());
            this.queryCard(jCardEntity);
            // 更新余额
            this.balanceCard(jCardEntity);
            // 是否需要通知api商户
            if (notify || jCardEntity.getApi().equals(1)) {
                // 通知商户
                JCardEntity entity = jCardDao.selectById(jCardEntity.getId());
                apiNotify.cardNewNotify(entity, merchant);
            }
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
            if (notify || jCardEntity.getApi().equals(1)) {
                // 发卡状态更新
                JCardEntity entity = jCardDao.selectById(jCardEntity.getId());
                JMerchantEntity merchant = jMerchantDao.selectById(jCardEntity.getMerchantId());
                apiNotify.cardChangeNotify(entity, merchant);
            }
        }
        // 其他情况
        jCardDao.update(null, updateWrapper);
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
        update.setCardstate(response.getCardstate());
        jCardEntity.setCardstate(response.getCardstate());

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

    // 批量操作-卡状态变更
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

    // 更新卡状态 + 卡的余额
    public void updateCard(Long id) {
        JCardEntity cardEntity = jCardDao.selectById(id);

        // 查询卡状态
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
        update.setCardstate(response.getCardstate());
        update.setBalance(balanceResponse.getBalance());
        jCardDao.updateById(update);
    }


    public void prepaidCharge(Long id, BigDecimal adjustAmount, int api) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        this.prepaidCharge(cardEntity, adjustAmount, api);
    }

    // 预付费卡-充值
    public void prepaidCharge(JCardEntity cardEntity, BigDecimal adjustAmount, int api) {
        JVpaAdjustEntity processing = jVpaAdjustDao.selectOne(Wrappers.<JVpaAdjustEntity>lambdaQuery()
                .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                .eq(JVpaAdjustEntity::getCardno, cardEntity.getCardno())
        );
        if (processing != null) {
            BigDecimal amount = processing.getAdjustAmount();
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                throw new RenException("有一笔" + amount + "充值进行中");
            }
            throw new RenException("有一笔" + amount + "提现进行中");
        }

        BigDecimal oldAuth = cardEntity.getAuthmaxamount();
        BigDecimal newAuth = cardEntity.getAuthmaxamount().add(adjustAmount);
        JVpaAdjustEntity adjustEntity = ConvertUtils.sourceToTarget(cardEntity, JVpaAdjustEntity.class);
        adjustEntity.setApi(api);
        adjustEntity.setId(null);
        adjustEntity.setCreateDate(null);
        adjustEntity.setUpdateDate(null);
        adjustEntity.setCreator(null);
        adjustEntity.setUpdater(null);
        adjustEntity.setMarketproduct(ZinConstant.MP_VPA_PREPAID);
        adjustEntity.setAdjustAmount(adjustAmount);
        adjustEntity.setOldQuota(oldAuth);
        adjustEntity.setNewQuota(newAuth);
        adjustEntity.setState(ZinConstant.VPA_ADJUST_UNKNOWN);
        adjustEntity.setMaincardno(cardEntity.getMaincardno());
        jVpaAdjustDao.insert(adjustEntity);

        // 冻结
        tx.executeWithoutResult(st -> {
            ledgerPrepaidCharge.ledgePrepaidChargeFreeze(adjustEntity);
        });

        // 发起变更
        try {
            TCardUpdateScene request = new TCardUpdateScene(cardEntity.getCurrency(), cardEntity.getCardno(), newAuth, null);
            TCardUpdateSceneResponse response = zinCardApplyService.cardUpdateScene(request);
            tx.executeWithoutResult(st -> {
                int update = jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                        .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                        .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                        .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_SUCCESS)
                        .set(JVpaAdjustEntity::getStatDate, new Date())  // 完成日期
                );
                if (update != 1) {
                    throw new RenException("更新调整失败");
                }
                // 更新卡的当前额度
                jCardDao.update(null, Wrappers.<JCardEntity>lambdaUpdate()
                        .eq(JCardEntity::getId, cardEntity.getId())
                        .set(JCardEntity::getAuthmaxamount, newAuth)
                );

                // confirm记账
                ledgerPrepaidCharge.ledgePrepaidCharge(adjustEntity);
            });
            CompletableFuture.runAsync(() -> {
                this.balanceCard(cardEntity);
            });
        } catch (Exception ex) {

            tx.executeWithoutResult(st -> {
                int update = jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                        .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                        .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                        .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_FAIL)
                );
                if (update != 1) {
                    throw new RenException("回滚失败");
                }
                // 查询通联修改是否成功， 再确定是否解冻释放, 更新调整失败
                ledgerPrepaidCharge.ledgePrepaidChargeUnFreeze(adjustEntity);
            });
        }
    }

    public void prepaidWithdraw(Long id, BigDecimal adjustAmount, int api) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        this.prepaidWithdraw(cardEntity, adjustAmount, api);
    }

    // 预付费卡-提现
    public void prepaidWithdraw(JCardEntity cardEntity, BigDecimal adjustAmount, int api) {

        // 如果卡有充提进行中
        JVpaAdjustEntity processing = jVpaAdjustDao.selectOne(Wrappers.<JVpaAdjustEntity>lambdaQuery()
                .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                .eq(JVpaAdjustEntity::getCardno, cardEntity.getCardno())
        );
        if (processing != null) {
            BigDecimal amount = processing.getAdjustAmount();
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                throw new RenException("有一笔" + amount + "充值进行中");
            }
            throw new RenException("有一笔" + amount + "提现进行中");
        }

        BigDecimal oldAuth = cardEntity.getAuthmaxamount();
        BigDecimal newAuth = cardEntity.getAuthmaxamount().subtract(adjustAmount);

        JVpaAdjustEntity adjustEntity = ConvertUtils.sourceToTarget(cardEntity, JVpaAdjustEntity.class);
        adjustEntity.setApi(api);
        adjustEntity.setId(null);
        adjustEntity.setCreateDate(null);
        adjustEntity.setUpdateDate(null);
        adjustEntity.setCreator(null);
        adjustEntity.setUpdater(null);
        adjustEntity.setMarketproduct(ZinConstant.MP_VPA_PREPAID);
        adjustEntity.setAdjustAmount(adjustAmount.negate());
        adjustEntity.setOldQuota(oldAuth);
        adjustEntity.setNewQuota(newAuth);
        adjustEntity.setState(ZinConstant.VPA_ADJUST_UNKNOWN);
        adjustEntity.setMaincardno(cardEntity.getMaincardno());
        jVpaAdjustDao.insert(adjustEntity);

        // 发起变更
        TCardUpdateScene request = new TCardUpdateScene(cardEntity.getCurrency(), cardEntity.getCardno(), newAuth, null);
        try {
            TCardUpdateSceneResponse response = zinCardApplyService.cardUpdateScene(request);
            tx.executeWithoutResult(st -> {
                int update = jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                        .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                        .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                        .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_SUCCESS)
                        .set(JVpaAdjustEntity::getStatDate, new Date())  // 完成日期
                );
                if (update != 1) {
                    throw new RenException("更新调整失败");
                }
                // 更新卡的当前额度
                jCardDao.update(null, Wrappers.<JCardEntity>lambdaUpdate()
                        .eq(JCardEntity::getId, cardEntity.getId())
                        .set(JCardEntity::getAuthmaxamount, newAuth)
                );
                // confirm记账
                ledgerPrepaidWithdraw.ledgePrepaidWithdraw(adjustEntity);
            });
            CompletableFuture.runAsync(() -> {
                this.balanceCard(cardEntity);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("failed");
        }
    }

    public void setQuota(Long id, BigDecimal authmaxamount, Integer authmaxcount, int api) {
        JCardEntity cardEntity = jCardDao.selectById(id);
        this.setQuota(cardEntity, authmaxamount, authmaxcount, api);
    }

    // 共享子卡设置额度
    public void setQuota(JCardEntity cardEntity, BigDecimal authmaxamount, Integer authmaxcount, int api) {
        BigDecimal oldAuth = cardEntity.getAuthmaxamount();
        BigDecimal newAuth = authmaxamount;
        BigDecimal adjustAmount = newAuth.subtract(oldAuth);

        JVpaAdjustEntity adjustEntity = ConvertUtils.sourceToTarget(cardEntity, JVpaAdjustEntity.class);
        adjustEntity.setApi(api);
        adjustEntity.setId(null);
        adjustEntity.setMarketproduct(ZinConstant.MP_VPA_SHARE);
        adjustEntity.setCreateDate(null);
        adjustEntity.setUpdateDate(null);
        adjustEntity.setCreator(null);
        adjustEntity.setUpdater(null);
        adjustEntity.setAdjustAmount(adjustAmount);
        adjustEntity.setOldQuota(oldAuth);
        adjustEntity.setNewQuota(newAuth);
        adjustEntity.setState(ZinConstant.VPA_ADJUST_UNKNOWN);
        adjustEntity.setMaincardno(cardEntity.getMaincardno());
        jVpaAdjustDao.insert(adjustEntity);

        if (authmaxcount == null) {
            authmaxcount = cardEntity.getAuthmaxcount();
        }

        TCardUpdateScene request = null;
        if (oldAuth.compareTo(newAuth) == 0) {
            request = new TCardUpdateScene(cardEntity.getCurrency(), cardEntity.getCardno(), null, authmaxcount);
        } else {
            request = new TCardUpdateScene(cardEntity.getCurrency(), cardEntity.getCardno(), authmaxamount, authmaxcount);
        }
        try {
            TCardUpdateSceneResponse response = zinCardApplyService.cardUpdateScene(request);
            // 调整成功
            tx.executeWithoutResult(st -> {
                jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                        .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                        .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                        .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_SUCCESS)
                        .set(JVpaAdjustEntity::getStatDate, new Date())  // 完成日期
                );
                // 更新卡的当前额度
                jCardDao.update(null, Wrappers.<JCardEntity>lambdaUpdate()
                        .eq(JCardEntity::getId, cardEntity.getId())
                        .set(JCardEntity::getAuthmaxamount, newAuth)
                );
            });

            this.balanceCard(cardEntity);
        } catch (Exception ex) {
            // 查询下， 如果明确失败
            jVpaAdjustDao.update(null, Wrappers.<JVpaAdjustEntity>lambdaUpdate()
                    .eq(JVpaAdjustEntity::getId, adjustEntity.getId())
                    .eq(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_UNKNOWN)
                    .set(JVpaAdjustEntity::getState, ZinConstant.VPA_ADJUST_FAIL)
            );
            ex.printStackTrace();
            throw ex;
        }
    }

}
