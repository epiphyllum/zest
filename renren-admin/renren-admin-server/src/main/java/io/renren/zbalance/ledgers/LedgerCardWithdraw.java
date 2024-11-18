package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LedgerCardWithdraw {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 卡资金退回: 冻结
    public void ledgeCardWithdrawFreeze(JWithdrawEntity entity, JSubEntity sub) {
        String factMemo = "冻结-卡资金提取:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(cardSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE_CARD_SUM, entity.getId(), factMemo, factAmount);

        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(charge, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE_CARD_CHARGE, entity.getId(), factMemo, entity.getMerchantfee().negate());

        // 预付费主卡提现
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            if (entity.getAmount().compareTo(ppMain.getBalance()) > 0) {
                throw new RenException("预付费主卡可用额度不足:" + ppMain.getBalance());
            }
            ledgerUtil.freezeUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡资金退回: 解冻
    public void ledgeCardWithdrawUnFreeze(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount();
        String factMemo = "解冻-卡资金提取:" + BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP);

        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(cardSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_UNFREEZE_CARD_SUM, entity.getId(), factMemo, factAmount);

        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(charge, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE_CARD_CHARGE, entity.getId(), factMemo, entity.getMerchantfee().negate());

        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity prepaidQuota = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.unFreezeUpdate(prepaidQuota, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_UNFREEZE_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-卡资金提取:" + showAmount;
        BigDecimal factAmount = entity.getAmount();

        // 记账1: 子商户-卡汇总资金
        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(cardSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_CONFIRM_CARD_SUM, entity.getId(), factMemo, factAmount);

        // 记账2: 子商户-手续费汇总
        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(charge, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_CHARGE, entity.getId(), factMemo, entity.getMerchantfee());

        // 记账3: 子商户-Va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_IN_SUB_VA, entity.getId(), factMemo, factAmount);

        // 记账4: 通联-手续费汇总
        JBalanceEntity aipCharge = ledgerUtil.getAipChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCharge, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_AIP_CHARGE, entity.getId(), factMemo, entity.getFee());

        // 记账5: 通联-卡充值汇总
        JBalanceEntity aipCardSum = ledgerUtil.getAipCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCardSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_AIP_CARD_SUM, entity.getId(), factMemo, entity.getAmount().negate());

        // 预付费主卡提现
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.confirmUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
        }
    }
}
