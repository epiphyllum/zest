package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
public class LedgerCardCharge {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    //
    public void ledgeCardChargeFreeze(JDepositEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount()
                .add(entity.getMerchantCharge())
                .add(entity.getMerchantDeposit());
        String factMemo = String.format("冻结-卡充值:%s, 到账:%s, 保证金:%s, 手续费:%s",
                factAmount,
                entity.getAmount(), entity.getMerchantDeposit(), entity.getMerchantCharge());

        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 取消卡充值, 卡充值失败
    public void ledgeCardChargeUnFreeze(JDepositEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount()
                .add(entity.getMerchantCharge())
                .add(entity.getMerchantDeposit());
        String factMemo = String.format("解冻-卡充值:%s, 到账:%s, 保证金:%s, 手续费:%s",
                factAmount,
                entity.getAmount(), entity.getMerchantDeposit(), entity.getMerchantCharge());
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 卡充值
    public void ledgeCardCharge(JDepositEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount()
                .add(entity.getMerchantCharge())
                .add(entity.getMerchantDeposit());
        String factMemo = String.format("确认-卡充值:%s, 到账:%s, 保证金:%s, 手续费:%s",
                factAmount,
                entity.getAmount(), entity.getMerchantDeposit(), entity.getMerchantCharge());

        ///////////////////////////////////////////////////////////////////////////////////
        // 记账1: 子商户-va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_CONFIRM_SUB_VA, entity.getId(), factMemo, factAmount);

        // 记账2: 子商户-发卡总额
        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal cardSumAmount = entity.getAmount();
        ledgerUtil.ledgeUpdate(cardSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_CARD_SUM, entity.getId(), factMemo, cardSumAmount);

        // 记账3: 子商户-保证金
        JBalanceEntity deposit = ledgerUtil.getDepositAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(deposit, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_DEPOSIT, entity.getId(), factMemo, entity.getMerchantDeposit());

        // 记账4: 子商户-手续费
        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(charge, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_CHARGE, entity.getId(), factMemo, entity.getMerchantCharge());

        ///////////////////////////////////////////////////////////////////////////////////
        // 通联-累计充值金额
        JBalanceEntity aipCardSum = ledgerUtil.getAipCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCardSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_AIP_CARD_SUM, entity.getId(), factMemo, entity.getTxnAmount());

        // 通联-累计保证金额
        JBalanceEntity depositSum = ledgerUtil.getAipDepositAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(depositSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_AIP_DEPOSIT, entity.getId(), factMemo, entity.getSecurityamount());

        // 通联-累计手续费金额
        JBalanceEntity aipCharge = ledgerUtil.getAipChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCharge, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_AIP_CHARGE, entity.getId(), factMemo, entity.getFee());

        // 预付费主卡充值
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity prepaidQuota = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.ledgeUpdate(prepaidQuota, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_PREPAID_MAIN, entity.getId(), factMemo, factAmount);
        }
    }
}