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
    public static final int ORIGIN_TYPE_WALLET_CHARGE = 610;          // 钱包充值
    public static final int FACT_WALLET_CHARGE_OUT_SUB_VA = 61000;    // 钱包入金
    public static final int FACT_WALLET_CHARGE_IN_WALLET = 61001;     // 钱包入金

    @Resource
    private LedgerUtil ledgerUtil;

    public void ledgeWalletCharge(JWalletTxnEntity entity) {
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getToAsset());
        if (walletAccount == null) {
            log.error("账号不存在: {} {}", entity.getWalletId(), entity.getToAsset());
            throw new RenException("账号不存在");
        }

        String factMemo = String.format("钱包充值:%s%s", entity.getToAmount(), entity.getToAsset());
        BigDecimal factAmount = entity.getToAmount();
        ledgerUtil.ledgeUpdate(walletAccount, ORIGIN_TYPE_WALLET_CHARGE, FACT_WALLET_CHARGE_IN_WALLET, entity.getId(), factMemo, factAmount);
    }

}
