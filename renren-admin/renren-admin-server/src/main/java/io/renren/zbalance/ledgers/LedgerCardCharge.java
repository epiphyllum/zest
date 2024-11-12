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
        String factMemo = "冻结卡充值:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 取消卡充值, 卡充值失败
    public void ledgeCardChargeUnFreeze(JDepositEntity entity, JSubEntity sub) {
        String factMemo = "解冻卡充值:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡充值
    public void ledgeCardCharge(JDepositEntity entity, JSubEntity sub) {
        BigDecimal showAmount = BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认卡充值:" + showAmount + ", 发起金额:" + entity.getTxnAmount() + ",担保金:" + entity.getSecurityamount() + ",手续费:" + entity.getFee();
        BigDecimal factAmount = entity.getAmount();
        // 记账1: 子商户subVa-
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_CONFIRM, entity.getId(), factMemo, factAmount);

        // 记账2: 子商户subSum+
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN, entity.getId(), factMemo, factAmount);

        // 累计充值金额
        JBalanceEntity chargeSum = ledgerUtil.getChargeSumAccount(0L, entity.getCurrency());
        ledgerUtil.ledgeUpdate(chargeSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_CHARGE_SUM, entity.getId(), factMemo, entity.getTxnAmount());

        // 累计保证金额
        JBalanceEntity depositSum = ledgerUtil.getDepositSumAccount(0L, entity.getCurrency());
        ledgerUtil.ledgeUpdate(depositSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_DEPOSIT_SUM, entity.getId(), factMemo, entity.getSecurityamount());

        // 累计手续费金额
        JBalanceEntity feeSum = ledgerUtil.getFeeSumAccount(0L, entity.getCurrency());
        ledgerUtil.ledgeUpdate(feeSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_FEE_SUM, entity.getId(), factMemo, entity.getFee());

        // 预付费主卡充值
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.ledgeUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN_PREPAID_MAIN, entity.getId(), factMemo, factAmount);
        }
    }
}