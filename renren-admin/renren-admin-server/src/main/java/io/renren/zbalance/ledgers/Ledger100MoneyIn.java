package io.renren.zbalance.ledgers;

import io.renren.zadmin.entity.JB2bEntity;
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
    public static final int ORIGIN_TYPE_MONEY = 100;  // 商户入金充值
    public static final int FACT_MONEY_IN_VA = 10001; // 1. 商户VA
    // 入金充值
    public static final int ORIGIN_TYPE_MONEY_B2B = 101;  // 商户入金充值
    public static final int FACT_MONEY_IN_VA_B2B = 10101; // 1. 商户VA

    @Resource
    private LedgerUtil ledgerUtil;

    // 原始凭证(100):  收到商户入金
    public void ledgeMoneyIn(JMoneyEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = "商户入金" + entity.getAmount() + entity.getCurrency();
        ledgerUtil.ledgeUpdate(vaAccount, ORIGIN_TYPE_MONEY, FACT_MONEY_IN_VA, entity.getId(), factMemo, entity.getAmount());
    }

    // 原始凭证(101): 独立入金
    public void ledgeMoneyInB2b(JB2bEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = "商户入金" + entity.getAmount() + entity.getCurrency();
        ledgerUtil.ledgeUpdate(vaAccount, ORIGIN_TYPE_MONEY_B2B, FACT_MONEY_IN_VA_B2B, entity.getId(), factMemo, entity.getAmount());
    }

}
