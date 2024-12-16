package io.renren.zbalance.ledgers;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 钱包提现: 退U
@Service
public class Ledger620WalletWithdraw {

    // 钱包提现
    public static final int ORIGIN_TYPE_WALLET_WITHDRAW = 620;                           // 钱包提现
    public static final int FACT_WALLET_WITHDRAW_OUT_WALLET = 62000;                     // 钱包冻结

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private Ledger604WalletCardOpenCharge ledger604WalletCardOpenCharge;

    // 钱包-提现
    public void ledgeWalletWithdraw(JWalletTxnEntity entity) {
        // 记账: 钱包-
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getFromCurrency());
        if (walletAccount.getBalance().compareTo(entity.getFromAmount()) < 0)  {
            throw new RenException("余额不足");
        }
        String factMemo = String.format("提现:%s %s", entity.getFromAmount(), entity.getFromCurrency());
        BigDecimal factAmount = entity.getFromAmount();
        ledgerUtil.freezeUpdate(walletAccount, ORIGIN_TYPE_WALLET_WITHDRAW, FACT_WALLET_WITHDRAW_OUT_WALLET, entity.getId(), factMemo, factAmount);
    }
}
