package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMfreeEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * 释放商户担保金
 */
@Service
@Slf4j
public class Ledger900Mfree {
    // 释放商户担保金
    public static final int ORIGIN_TYPE_MFREE = 900;               // 释放担保金
    public static final int FACT_MFREE_OUT = 90001;                // 1. 商户保证账户 -100HKD
    public static final int FACT_MFREE_IN = 90002;                 // 2. 商户账户  +100HKD

    @Resource
    private LedgerUtil ledgerUtil;

    // 释放商户担保金
    public void ledgeMfree(JMfreeEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity depVa = ledgerUtil.getDepositAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = String.format("释放担保金:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户担保金-
        ledgerUtil.ledgeUpdate(depVa, ORIGIN_TYPE_MFREE, FACT_MFREE_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        // 记账2: 商户Va+
        ledgerUtil.ledgeUpdate(mVa, ORIGIN_TYPE_MFREE, FACT_MFREE_IN, entity.getId(), factMemo, entity.getAmount());
    }
}
