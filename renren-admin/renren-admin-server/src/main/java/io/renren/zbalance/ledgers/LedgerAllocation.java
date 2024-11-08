package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LedgerAllocation {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 商户va ---> 子商户va
    public void ledgeM2s(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("转入子商户:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户Va-
        ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_M2S, LedgerConstant.FACT_M2S_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        ledgerUtil.ledgeUpdate(sVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_M2S, LedgerConstant.FACT_M2S_IN, entity.getId(), factMemo, entity.getAmount());
    }

    // 子商户va -->  商户va
    public void ledgeS2m(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("转出子商户:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 子商户Va-
        ledgerUtil.ledgeUpdate(sVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_S2M, LedgerConstant.FACT_S2M_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        // 记账2: 商户Va+
        ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_S2M, LedgerConstant.FACT_S2M_IN, entity.getId(), factMemo, entity.getAmount());
    }
}