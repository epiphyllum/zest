package io.renren.zbalance.ledgers;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 钱包兑换
@Service
@Slf4j
public class Ledger610WalletSwap {
    // 钱包充值
    public static final int ORIGIN_TYPE_WALLET_SWAP = 610;          // 钱包充值
    public static final int FACT_WALLET_SWAP_OUT_SUB_VA = 61000;    // 钱包入金
    public static final int FACT_WALLET_SWAP_IN_WALLET = 61001;     // 钱包入金

    @Resource
    private LedgerUtil ledgerUtil;

    public void ledgeWalletSwap(JWalletTxnEntity entity) {
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getToCurrency());
        if (walletAccount == null) {
            log.error("账号不存在: {} {}", entity.getWalletId(), entity.getToCurrency());
            throw new RenException("账号不存在");
        }
        String factMemo = String.format("钱包充值:%s%s", entity.getToAmount(), entity.getToCurrency());
        BigDecimal factAmount = entity.getToAmount();
        ledgerUtil.ledgeUpdate(walletAccount, ORIGIN_TYPE_WALLET_SWAP, FACT_WALLET_SWAP_IN_WALLET, entity.getId(), factMemo, factAmount);
    }

}
