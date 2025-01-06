package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 子商户-商户  va互转
@Service
@Slf4j
public class Ledger300Allocation {
    // 资金调拨-转入子商户VA
    public static final int ORIGIN_TYPE_ALLOCATE_M2S = 300;                   // 资金调拨-转入子商户VA
    public static final int FACT_M2S_IN_SUB_VA = 30001;                       // 1. 商户VA
    public static final int FACT_M2S_OUT_VA = 30002;                          // 2. 子商户VA
    // 资金调拨-转出子商户VA
    public static final int ORIGIN_TYPE_ALLOCATE_S2M = 400;                   // 资金调拨-转出子商户VA
    public static final int FACT_S2M_IN_VA = 40001;                           // 1. 子商户VA
    public static final int FACT_S2M_OUT_SUB_VA = 40002;                      // 2. 商户VA

    @Resource
    private LedgerUtil ledgerUtil;

    // 商户va ---> 子商户va
    public void ledgeM2s(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("转入子商户:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户Va-
        ledgerUtil.ledgeUpdate(mVa, ORIGIN_TYPE_ALLOCATE_M2S, FACT_M2S_OUT_VA, entity.getId(), factMemo, entity.getAmount().negate());
        ledgerUtil.ledgeUpdate(sVa, ORIGIN_TYPE_ALLOCATE_M2S, FACT_M2S_IN_SUB_VA, entity.getId(), factMemo, entity.getAmount());
    }

    // 子商户va -->  商户va
    public void ledgeS2m(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("转出子商户:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 子商户Va-
        ledgerUtil.ledgeUpdate(sVa, ORIGIN_TYPE_ALLOCATE_S2M, FACT_S2M_OUT_SUB_VA, entity.getId(), factMemo, entity.getAmount().negate());
        // 记账2: 商户Va+
        ledgerUtil.ledgeUpdate(mVa, ORIGIN_TYPE_ALLOCATE_S2M, FACT_S2M_IN_VA, entity.getId(), factMemo, entity.getAmount());
    }

}