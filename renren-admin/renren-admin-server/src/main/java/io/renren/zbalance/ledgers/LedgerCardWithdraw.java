package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
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
        String factMemo = "冻结卡资金提取:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡资金退回: 解冻
    public void ledgeCardWithdrawUnFreeze(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        String factMemo = "确认卡资金提取:" + BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP);
        ledgerUtil.unFreezeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JWithdrawEntity entity, JSubEntity sub) {
        // 子商户va, 卡汇总充值资金账号
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认卡资金提取:" + showAmount;
        BigDecimal factAmount = entity.getAmount();
        // 记账1: 子商户Va-confirm
        ledgerUtil.confirmUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_CONFIRM, entity.getId(), factMemo, factAmount);
        // 记账2: 子商户Va+
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_IN, entity.getId(), factMemo, factAmount);
        // 预付费主卡提现: todo
    }

    // 释放商户担保金
    public void ledgeMfree(JMfreeEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity depVa = ledgerUtil.getDepositAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = String.format("释放担保金:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户担保金-
        ledgerUtil.ledgeUpdate(depVa, LedgerConstant.ORIGIN_TYPE_MFREE, LedgerConstant.FACT_MFREE_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        // 记账2: 商户Va+
        ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_MFREE, LedgerConstant.FACT_MFREE_IN, entity.getId(), factMemo, entity.getAmount());
    }
}
