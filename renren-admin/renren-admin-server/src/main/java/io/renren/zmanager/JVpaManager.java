package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.LedgerUtil;
import io.renren.zbalance.ledgers.LedgerOpenVpa;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class JVpaManager {

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
    private LedgerOpenVpa ledgerOpenVpa;
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

    //
    private void checkExpiredate(JVpaJobEntity entity, String mainCardExpiredate) {
        // token有效期: YYYY-MM-DD
        String cardexpiredate = entity.getCardexpiredate();
        Pattern pCardexpiredate = Pattern.compile("\\d\\d(\\d\\d)-(\\d\\d)-(\\d\\d)");
        Matcher pCardexpiredateMatcher = pCardexpiredate.matcher(cardexpiredate);
        if (!pCardexpiredateMatcher.matches()) {
            throw new RenException("有效期时间格式错误");
        }

        String cardexpiredateNorm = pCardexpiredateMatcher.group(1) + pCardexpiredateMatcher.group(2);
        if (mainCardExpiredate.compareTo(cardexpiredateNorm) < 0) {
            throw new RenException("要求有效期时间不能晚于主卡有效期:" + mainCardExpiredate);
        }

        // 期限enddate： YYYY-MM-DD
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
            throw new RenException("invalid request");
        }

        // 查询主卡
        JCardEntity mainCard = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, entity.getMaincardno())
        );
        String mainCardExpiredate = mainCard.getExpiredate();

        // 检查卡有效期设置
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

        // 计算批次发卡手续费
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        BigDecimal price = null;
        String feecurrency = "HKD";
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_SHARE)) {
            price = merchant.getVpaShareFee();
        } else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            price = merchant.getVpaPrepaidFee();
        } else {
            throw new RenException("非法请求");
        }
        BigDecimal totalMerchantFee = price.multiply(new BigDecimal(entity.getNum()));
        entity.setFeecurrency(feecurrency);
        entity.setMerchantfee(totalMerchantFee);

        // 发行预付费子卡, 需要判断主卡是否有足额
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            BigDecimal totalAuth = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
            JBalanceEntity prepaidAccount = ledgerUtil.getPrepaidAccount(mainCard.getId(), mainCard.getCurrency());
            if (prepaidAccount.getBalance().compareTo(totalAuth) < 0) {
                throw new RenException("余额不足");
            }
        }

        // 调用通联创建模板
        entity.setScenename(CommonUtils.uniqueId());
        TCardAddScene request = ConvertUtils.sourceToTarget(entity, TCardAddScene.class);
        TCardAddSceneResponse response = zinCardApplyService.cardAddScene(request);

        entity.setSceneid(response.getSceneid());


        entity.setState(ZinConstant.CARD_APPLY_NEW_DJ);

        log.info("vpa open card, fee:{}", totalMerchantFee);

        // 记账 + 入库
        tx.executeWithoutResult(st -> {
            jVpaJobDao.insert(entity);
            ledgerOpenVpa.ledgeOpenVpaFreeze(entity);
        });
    }

    // 提交通联
    public void submit(JVpaJobEntity entity) {
        TCardVpaApply request = ConvertUtils.sourceToTarget(entity, TCardVpaApply.class);
        TCardVpaApplyResponse response = zinCardApplyService.cardVpaApply(request);

        String applyid = response.getApplyid();
        JVpaJobEntity update = new JVpaJobEntity();
        update.setId(entity.getId());
        update.setApplyid(applyid);
        jVpaJobDao.updateById(update);
        entity.setApplyid(applyid);
    }

    // 填充卡列表, 初始额度调整列表
    public void vpaInitFill(JVpaJobEntity entity, List<JCardEntity> cards, List<JVpaAdjustEntity> adjusts, JCardEntity mainCard) {
        BigDecimal merchantFee = entity.getMerchantfee().divide(new BigDecimal(entity.getNum()), 2, RoundingMode.HALF_UP);
        for (JCardEntity jCardEntity : cards) {
            jCardEntity.setMaincardno(entity.getMaincardno());

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
            jCardEntity.setCardState(ZinConstant.CARD_STATE_SUCCESS);
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

            // 币种， 开卡费
            jCardEntity.setFeecurrency(entity.getFeecurrency());
            jCardEntity.setMerchantfee(merchantFee);
            // cvv加密入库
            jCardEntity.setCvv(CommonUtils.encryptSensitiveString(jCardEntity.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "UTF-8"));

            // 初始额度记录
            JVpaAdjustEntity adjustItem = new JVpaAdjustEntity();
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
            log.info("adjustItem:{}", adjustItem);
            adjusts.add(adjustItem);
        }
    }

    // 开卡任务查询
    public void query(JVpaJobEntity entity, boolean notify) {
        TCardApplyQuery query = new TCardApplyQuery();
        query.setApplyid(entity.getApplyid());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        // 从非失败  -> 失败,  处理退款
        String prevState = entity.getState();
        String nextState = response.getState();
        log.info("vpa发卡查询结果:{}, prevState:{}, nextState:{}", response, prevState, nextState);

        JCardEntity mainCard = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, entity.getMaincardno()));

        // 不成功 -> 成功
        if (!ZinConstant.isCardApplySuccess(prevState) && ZinConstant.isCardApplySuccess(nextState)) {
            log.info("vpa发卡 不成功 --> 成功");

            // 下载文件, 拿到卡信息
            DownloadVpaRequest request = new DownloadVpaRequest();
            request.setApplyid(entity.getApplyid());
            List<VpaInfoItem> vpaInfoItems = zinFileService.downloadVapInfoAes(request);

            // 组织为card记录
            List<JCardEntity> jCardEntities = ConvertUtils.sourceToTarget(vpaInfoItems, JCardEntity.class);
            List<JVpaAdjustEntity> adjustEntities = new ArrayList<>(entity.getNum());

            // 期限卡: 额度记录
            if (entity.getCycle().equals(ZinConstant.VPA_CYCLE_DEADLINE) ||
                    entity.getCycle().equals(ZinConstant.VPA_CYCLE_PERIODICAL) ||
                    (entity.getCycle().equals(ZinConstant.VPA_CYCLE_ONCE) && entity.getFeecurrency().equals("Y"))
            ) {
                vpaInitFill(entity, jCardEntities, adjustEntities, mainCard);
            }

            // 插入卡表， 记账扣费, 更新任务状态:  200
            tx.executeWithoutResult(st -> {
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
                );
                // 记账
                ledgerOpenVpa.ledgeOpenVpa(entity);
            });
        }
        // 非失败 -> 失败
        else if (!ZinConstant.isCardApplyFail(prevState) && ZinConstant.isCardApplyFail(nextState)) {
            log.info("vpa发卡 非失败 --> 失败");
        }

        // vpa我们对外接口还没提供
        if (entity.getApi().equals(1) && notify) {
        }
    }
}