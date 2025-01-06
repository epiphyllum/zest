package io.renren.zbalance.ledgers;

import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Ledger100MoneyIn {
    // 入金充值
    public static final int ORIGIN_TYPE_MONEY = 100;                          // 入金充值
    public static final int FACT_MONEY_IN_VA = 10001;                         // 1. 商户VA        +94HKD

    @Resource
    private LedgerUtil ledgerUtil;

    // 原始凭证(100):  收到商户入金
    public void ledgeMoneyIn(JMoneyEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = "入金:" + entity.getAmount() + entity.getCurrency();
        ledgerUtil.ledgeUpdate(vaAccount, ORIGIN_TYPE_MONEY, FACT_MONEY_IN_VA, entity.getId(), factMemo, entity.getAmount());
    }

}
