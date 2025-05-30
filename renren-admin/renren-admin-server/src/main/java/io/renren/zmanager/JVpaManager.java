package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.LedgerUtil;
import io.renren.zbalance.ledgers.Ledger502OpenVpaPrepaid;
import io.renren.zbalance.ledgers.Ledger501OpenVpaShare;
import io.renren.zbalance.ledgers.Ledger503OpenVpaWallet;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.BankException;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardapply.dto.*;
import io.renren.zin.file.ZinFileService;
import io.renren.zin.file.dto.DownloadVpaRequest;
import io.renren.zin.file.dto.VpaInfoItem;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class JVpaManager {

    @Resource
    private JDepositManager jDepositManager;
    @Resource
    private JCommon jCommon;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JVpaJobDao jVpaJobDao;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ApiNotify apiNotify;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private Ledger501OpenVpaShare ledger501OpenVpaShare;
    @Resource
    private Ledger502OpenVpaPrepaid ledger502OpenVpaPrepaid;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JVpaAdjustDao jVpaAdjustDao;
    @Resource
    private JConfigDao jConfigDao;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JFeeConfigDao jFeeConfigDao;
    @Resource
    private Ledger503OpenVpaWallet ledger503OpenVpaWallet;

    //
    private void checkExpiredate(JVpaJobEntity entity, String mainCardExpiredate) {
        // token有效期: YYYY-MM-DD
        String cardexpiredate = entity.getCardexpiredate();
        Pattern pCardexpiredate = Pattern.compile("\\d\\d(\\d\\d)-(\\d\\d)-(\\d\\d)");
        Matcher pCardexpiredateMatcher = pCardexpiredate.matcher(cardexpiredate);
        if (!pCardexpiredateMatcher.matches()) {
            throw new RenException("有效期时间格式错误");
        }

        // 设置的token有效期， 要在主卡有效期之前
        String cardexpiredateNorm = pCardexpiredateMatcher.group(1) + pCardexpiredateMatcher.group(2);
        if (mainCardExpiredate.compareTo(cardexpiredateNorm) < 0) {
            throw new RenException("要求有效期时间不能晚于主卡有效期:" + mainCardExpiredate);
        }

        // 期限卡: 额外限制:enddate： YYYY-MM-DD
        if (entity.getCycle().equals(ZinConstant.VPA_CYCLE_DEADLINE)) {
            String enddate = entity.getEnddate();
            Pattern pEnddate = Pattern.compile("\\d\\d(\\d\\d)-(\\d\\d)-(\\d\\d)");
            Matcher enddateMatcher = pEnddate.matcher(enddate);
            if (!enddateMatcher.matches()) {
                throw new RenException("期限结束时间格式错误");
            }
            String enddateNorm = enddateMatcher.group(1) + enddateMatcher.group(2);
            if (mainCardExpiredate.compareTo(enddateNorm) < 0) {
                throw new RenException("期限结束时间不能晚于" + mainCardExpiredate);
            }
        }
    }

    // 保持vpa发卡任务
    public void save(JVpaJobEntity entity) {
        Long subId = entity.getSubId();
        if (subId == null) {
            throw new RenException("缺少字段:subId");
        }

        // 不限制交易笔数
        if (entity.getAuthmaxcount() == null) {
            entity.setAuthmaxcount(0);
        }

        // 查询主卡
        JCardEntity mainCard = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, entity.getMaincardno())
        );
        String mainCardExpiredate = mainCard.getExpiredate();
        entity.setMaincardid(mainCard.getId());
        entity.setMaincardno(mainCard.getCardno());

        // 预付费子卡有效期设置为活动截止日期
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            entity.setCardexpiredate(entity.getEnddate());
        }

        // 检查卡有效期设置
        log.info("main card expire:{}", mainCardExpiredate);
        checkExpiredate(entity, mainCardExpiredate);

        // 检查大批次发卡是否超限
        JConfigEntity gConfig = jConfigDao.selectOne(Wrappers.emptyWrapper());
        if (entity.getNum() > gConfig.getQuotaLimit()) {
            throw new RenException("单批次最大发卡数量:" + gConfig.getQuotaLimit());
        }

        // 关联ID
        JSubEntity sub = jSubDao.selectById(entity.getSubId());
        entity.setAgentId(sub.getAgentId());
        entity.setAgentName(sub.getAgentName());
        entity.setMerchantId(sub.getMerchantId());
        entity.setMerchantName(sub.getMerchantName());
        entity.setSubName(sub.getCusname());

        // 商户成本收入配置
        JFeeConfigEntity feeConfig = jCommon.getFeeConfig(sub.getMerchantId(), entity.getMarketproduct(), mainCard.getCurrency());
        entity.setProductcurrency(feeConfig.getCurrency());

        // 计算批次发卡手续费
        BigDecimal price = feeConfig.getCardFee();
        BigDecimal totalMerchantFee = price.multiply(new BigDecimal(entity.getNum()));
        entity.setMerchantfee(totalMerchantFee);

        // 发行预付费子卡, 需要判断主卡是否有足额
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            BigDecimal totalAuth = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
            JBalanceEntity prepaidQuotaAccount = ledgerUtil.getPrepaidQuotaAccount(mainCard.getId(), mainCard.getCurrency());
            if (prepaidQuotaAccount.getBalance().compareTo(totalAuth) < 0) {
                log.error("总授权: {}, 可发卡额: {}", totalAuth, prepaidQuotaAccount.getBalance());
                throw new RenException("子卡发卡失败, 余额不足");
            }
        }
        // 发行钱包子卡, 不需要判断钱包主卡的额度

        // 调用通联创建模板
        entity.setScenename(CommonUtils.uniqueId());
        TCardAddScene request = ConvertUtils.sourceToTarget(entity, TCardAddScene.class);
        TCardAddSceneResponse response = zinCardApplyService.cardAddScene(request);

        // 填充场景信息
        entity.setSceneid(response.getSceneid());
        // 初始状态
        entity.setState(ZinConstant.CARD_APPLY_NEW_DJ);

        // 记账 + 入库
        try {
            tx.executeWithoutResult(st -> {
                // 入库
                jVpaJobDao.insert(entity);
                // 共享子卡冻结
                if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_SHARE)) {
                    ledger501OpenVpaShare.ledgeOpenVpaShareFreeze(entity);
                }
                // 预付费子卡冻结
                else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
                    ledger502OpenVpaPrepaid.ledgeOpenVpaPrepaidFreeze(entity);
                }
                // 钱包子卡冻结
                else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
                    ledger503OpenVpaWallet.ledgeOpenVpaWalletFreeze(entity);
                }
            });
        } catch (Exception e) {
            log.error("VPA发卡记账失败, 发卡任务:{}", entity);
            e.printStackTrace();
            throw e;
        }
    }

    // 提交通联
    public void submit(JVpaJobEntity entity) {
        TCardVpaApply request = ConvertUtils.sourceToTarget(entity, TCardVpaApply.class);
        TCardVpaApplyResponse response = null;
        try {
            response = zinCardApplyService.cardVpaApply(request);
        } catch (BankException be) {
            // 取消
            this.cancel(entity);
            throw new RenException(be.getMessage());
        }

        String applyid = response.getApplyid();
        JVpaJobEntity update = new JVpaJobEntity();
        update.setId(entity.getId());
        update.setApplyid(applyid);
        jVpaJobDao.updateById(update);
        entity.setApplyid(applyid);
    }

    // 填充卡列表, 初始额度调整列表
    public void vpaInitFill(JVpaJobEntity entity, List<JCardEntity> cards, List<JVpaAdjustEntity> adjusts, JCardEntity mainCard, Date statDate) {
        JFeeConfigEntity feeConfig = jCommon.getFeeConfig(entity.getMerchantId(), entity.getMarketproduct(), mainCard.getCurrency());

        BigDecimal merchantFee = entity.getMerchantfee().divide(new BigDecimal(entity.getNum()), 2, RoundingMode.HALF_UP);
        for (JCardEntity jCardEntity : cards) {
            jCardEntity.setMaincardno(entity.getMaincardno());
            jCardEntity.setMaincardid(entity.getMaincardid());
            jCardEntity.setMarketproduct(entity.getMarketproduct());

            // 允许交易币种
            String permitCurrency = jCardEntity.getCurrency();
            jCardEntity.setPermitCurrency(permitCurrency);

            // 填充id
            jCardEntity.setAgentId(entity.getAgentId());
            jCardEntity.setAgentName(entity.getAgentName());
            jCardEntity.setMerchantId(entity.getMerchantId());
            jCardEntity.setMerchantName(entity.getMerchantName());
            jCardEntity.setSubId(entity.getSubId());
            jCardEntity.setSubName(entity.getSubName());

            //
            jCardEntity.setState(ZinConstant.CARD_APPLY_SUCCESS);
            jCardEntity.setCardstate(ZinConstant.CARD_STATE_SUCCESS);
            jCardEntity.setProducttype(ZinConstant.CARD_PRODUCT_VPA);
            jCardEntity.setMarketproduct(entity.getMarketproduct());
            jCardEntity.setCurrency(mainCard.getCurrency());  // 卡的币种为主卡的币种

            // 卡场景信息
            jCardEntity.setBegindate(entity.getBegindate());
            jCardEntity.setEnddate(entity.getEnddate());
            jCardEntity.setCycle(entity.getCycle());
            jCardEntity.setAuthmaxamount(entity.getAuthmaxamount());
            jCardEntity.setAuthmaxcount(entity.getAuthmaxcount());
            jCardEntity.setNaturalmonthflag(entity.getNaturalmonthflag());
            jCardEntity.setNaturalmonthstartday(entity.getNaturalmonthstartday());
            jCardEntity.setFixedamountflag(entity.getFixedamountflag());
            jCardEntity.setOnlhkflag(entity.getOnlhkflag());
            jCardEntity.setVpaJob(entity.getId());               // 那个发卡任务发的卡

            // 开卡费(收入) + (成本)
            jCardEntity.setMerchantfee(merchantFee);
            jCardEntity.setFee(feeConfig.getCostCardFee());

            // 不是接口创建的
            jCardEntity.setApi(0);
            jCardEntity.setMeraplid(CommonUtils.uniqueId());

            // 钱包ID
            jCardEntity.setWalletId(entity.getWalletId());

            // 完成日期
            jCardEntity.setStatDate(statDate);

            // cvv加密入库
            jCardEntity.setCvv(CommonUtils.encryptSensitiveString(jCardEntity.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "UTF-8"));

            // 初始额度记录
            JVpaAdjustEntity adjustItem = new JVpaAdjustEntity();
            adjustItem.setMarketproduct(entity.getMarketproduct());

            adjustItem.setAgentId(entity.getAgentId());
            adjustItem.setAgentName(entity.getAgentName());
            adjustItem.setMerchantId(entity.getMerchantId());
            adjustItem.setMerchantName(entity.getMerchantName());
            adjustItem.setSubId(entity.getSubId());
            adjustItem.setSubName(entity.getSubName());

            adjustItem.setOldQuota(BigDecimal.ZERO);
            adjustItem.setNewQuota(entity.getAuthmaxamount());
            adjustItem.setAdjustAmount(entity.getAuthmaxamount());
            adjustItem.setState(ZinConstant.VPA_ADJUST_SUCCESS);

            adjustItem.setCardno(jCardEntity.getCardno());
            adjustItem.setMaincardno(jCardEntity.getMaincardno());
            adjustItem.setMaincardid(entity.getMaincardid());
            adjustItem.setCurrency(entity.getProductcurrency());
            adjustItem.setWalletId(entity.getWalletId());
            adjustItem.setWalletName(entity.getWalletName());
            adjustItem.setApi(0);
            adjustItem.setMeraplid(CommonUtils.uniqueId());

            // 完成日期
            adjustItem.setStatDate(statDate);

            log.info("adjustItem:{}", adjustItem);
            adjusts.add(adjustItem);
        }
    }


    // vpa发卡成功
    public void querySuccess(JVpaJobEntity entity, String prevState, String newState) {
        log.info("vpa发卡 不成功 --> 成功");
        // 下载文件, 拿到卡信息
        DownloadVpaRequest request = new DownloadVpaRequest();
        request.setApplyid(entity.getApplyid());
        List<VpaInfoItem> vpaInfoItems = zinFileService.downloadVapInfoAes(request);
        if (vpaInfoItems.size() != entity.getNum()) {
            log.info("下载vcc信息条数与发卡数不符");
            throw new RenException("发卡异常, 请联系管理员");
        }

        // 组织为card记录
        List<JCardEntity> jCardEntities = ConvertUtils.sourceToTarget(vpaInfoItems, JCardEntity.class);
        List<JVpaAdjustEntity> adjustEntities = new ArrayList<>(entity.getNum());
        JCardEntity mainCard = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, entity.getMaincardno()));

        // 完成日期
        Date statDate = new Date();

        // 期限卡: 额度记录
        if (entity.getCycle().equals(ZinConstant.VPA_CYCLE_DEADLINE) ||
                entity.getCycle().equals(ZinConstant.VPA_CYCLE_PERIODICAL) ||
                // 单次卡, 且单次固定消费金额
                (
                        entity.getCycle().equals(ZinConstant.VPA_CYCLE_ONCE) &&
                                entity.getFixedamountflag().equals("Y")
                )
        ) {
            log.info("初始化发卡数据...");
            vpaInitFill(entity, jCardEntities, adjustEntities, mainCard, statDate);
        }

        // 插入卡表， 记账扣费, 更新任务状态:  200
        try {
            tx.executeWithoutResult(status -> {
                // 插入卡数据
                for (JCardEntity jCardEntity : jCardEntities) {
                    jCardDao.insert(jCardEntity);
                }

                // 插入卡初始额度数据
                for (JVpaAdjustEntity adjustEntity : adjustEntities) {
                    jVpaAdjustDao.insert(adjustEntity);
                }

                // 更新任务记录
                jVpaJobDao.update(null, Wrappers.<JVpaJobEntity>lambdaUpdate()
                        .eq(JVpaJobEntity::getId, entity.getId())
                        .eq(JVpaJobEntity::getState, prevState)
                        .set(JVpaJobEntity::getState, ZinConstant.CARD_APPLY_SUCCESS)
                        .set(JVpaJobEntity::getStatDate, statDate)
                );

                // 共享子卡记账
                if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_SHARE)) {
                    ledger501OpenVpaShare.ledgeOpenVpaShare(entity);
                }
                // 预付费子卡卡记账
                else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
                    ledger502OpenVpaPrepaid.ledgeOpenVpaPrepaid(entity);
                }
                // 钱包子卡记账
                else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
                    ledger503OpenVpaWallet.ledgeOpenVpaWallet(entity);
                } else {
                    throw new RenException("未知发卡类型");
                }
            });
        } catch (Exception e) {
            log.error("开卡记账失败, 任务:{}", entity);
            e.printStackTrace();
            throw e;
        }

        // 更新所有余额账号余额
        for (JCardEntity jCardEntity : jCardEntities) {
            CompletableFuture.runAsync(() -> {
                jCardManager.balanceCard(jCardEntity);
            });
        }
        entity.setState(newState);
    }

    // vpa发卡失败
    public void queryFail(JVpaJobEntity entity) {
        log.info("发卡 非失败 --> 失败");
        tx.executeWithoutResult(status -> {
            // 共享子卡记账
            if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_SHARE)) {
                ledger501OpenVpaShare.ledgeOpenVpaShareUnFreeze(entity);
            }
            // 预付费子卡卡记账
            else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
                ledger502OpenVpaPrepaid.ledgeOpenVpaPrepaidUnFreeze(entity);
            }
            // 钱包子卡记账
            else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
                ledger503OpenVpaWallet.ledgeOpenVpaWalletUnFreeze(entity);
            } else {
                throw new RenException("未知发卡类型");
            }

        });

    }

    // 开卡任务查询
    public void query(JVpaJobEntity entity, boolean notify) {
        log.info("查询发卡任务: {}", entity);
        TCardApplyQuery query = new TCardApplyQuery();
        query.setApplyid(entity.getApplyid());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);


        // 从非失败  -> 失败,  处理退款
        String prevState = entity.getState();
        String nextState = response.getState();
        log.info("vpa发卡查询结果, prevState:{}, nextState:{}", prevState, nextState);

        // 注意这行代码位置！！！
        entity.setState(response.getState());

        // 不成功 -> 成功
        if (!ZinConstant.isCardApplySuccess(prevState) && ZinConstant.isCardApplySuccess(nextState)) {
            querySuccess(entity, prevState, nextState);

            // 如果是钱包子卡发卡成功, 还需要给主卡充值
            JDepositEntity deposit = new JDepositEntity();
            deposit.setAmount(entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP));
            deposit.setCardno(entity.getMaincardno());
            deposit.setSubId(entity.getSubId());
            deposit.setCurrency(entity.getProductcurrency());
            deposit.setApi(0);
            deposit.setMeraplid(CommonUtils.uniqueId());
            jDepositManager.saveAndSubmit(deposit, true);

            // 如果需要通知商户
            if (notify) {
                JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
                apiNotify.vpaJobNotify(entity, merchant);
            }

        }
        // 非失败 -> 失败
        else if (!ZinConstant.isCardApplyFail(prevState) && ZinConstant.isCardApplyFail(nextState)) {
            queryFail(entity);
            // 如果需要通知商户
            if (notify) {
                JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
                apiNotify.vpaJobNotify(entity, merchant);
            }
        }


    }

    /**
     * 取消发卡任务
     *
     * @param entity
     */
    public void cancel(JVpaJobEntity entity) {
        try {
            tx.executeWithoutResult(status -> {
                int update = jVpaJobDao.update(null, Wrappers.<JVpaJobEntity>lambdaUpdate()
                        .eq(JVpaJobEntity::getId, entity.getId())
                        .set(JVpaJobEntity::getState, ZinConstant.CARD_APPLY_CLOSE)
                );
                if (update != 1) {
                    throw new RenException("取消失败");
                }
                // 共享子卡-取消发卡
                if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_SHARE)) {
                    ledger501OpenVpaShare.ledgeOpenVpaShareUnFreeze(entity);
                }
                // 预付费子卡-取消发卡
                else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
                    ledger502OpenVpaPrepaid.ledgeOpenVpaPrepaidUnFreeze(entity);
                }
                // 钱包子卡-取消发卡
                else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
                    ledger503OpenVpaWallet.ledgeOpenVpaWalletUnFreeze(entity);
                } else {
                    throw new RenException("未知发卡类型");
                }
            });
        } catch (Exception e) {
            log.error("取消开卡失败, 任务:{}", entity);
            e.printStackTrace();
            throw e;
        }
    }
}
