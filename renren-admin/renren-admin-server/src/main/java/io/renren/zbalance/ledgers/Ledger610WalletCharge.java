package io.renren.zbalance.ledgers;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 钱包充值: 收款成功
@Service
@Slf4j
public class Ledger610WalletCharge {
    // 钱包充值
    public static final int ORIGIN_TYPE_WALLET_CHARGE = 610;                   // 钱包充值
    public static final int FACT_WALLET_CHARGE_OUT_SUB_VA = 61000;              // 钱包入金
    public static final int FACT_WALLET_CHARGE_IN_WALLET = 61001;              // 钱包入金

    @Resource
    private LedgerUtil ledgerUtil;

    public void ledgeWalletCharge(JWalletTxnEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());
        if (walletAccount == null) {
            log.error("账号不存在: {} {}", entity.getWalletId(), entity.getCurrency());
            throw new RenException("账号不存在");
        }

        String factMemo = String.format("充值:%s%s, 到账:%s%s",
                entity.getPayAmount(), entity.getPayCurrency(),
                entity.getStlAmount(), entity.getCurrency()
        );
        BigDecimal factAmount = entity.getStlAmount();
        ledgerUtil.ledgeUpdate(subVa, ORIGIN_TYPE_WALLET_CHARGE, FACT_WALLET_CHARGE_OUT_SUB_VA, entity.getId(), factMemo, factAmount);
        ledgerUtil.ledgeUpdate(walletAccount, ORIGIN_TYPE_WALLET_CHARGE, FACT_WALLET_CHARGE_IN_WALLET, entity.getId(), factMemo, factAmount);
    }

}
