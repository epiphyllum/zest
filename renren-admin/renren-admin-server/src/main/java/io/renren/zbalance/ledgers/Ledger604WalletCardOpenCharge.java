package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class Ledger604WalletCardOpenCharge {

    // 钱包子卡-批量开卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_WALLET_CARD_OPEN_CHARGE = 604;                   // 钱包子卡-批量开卡卡充值
    public static final int FACT_WALLET_CARD_OPEN_CHARGE_IN_WALLET_SUM = 60400;          // 钱包子卡发卡总额

    @Resource
    private LedgerUtil ledgerUtil;


    //钱包子卡-批量开卡充值确认(调整主卡可用额度)
    public void ledgeWalletOpenCharge(JVpaJobEntity entity) {
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("确认-批量开通%s张卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        // 记账1: 累计钱包主卡的子卡发卡总额
        JBalanceEntity walletSumBalance = ledgerUtil.getWalletSumAccount(entity.getMaincardid(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(walletSumBalance, ORIGIN_TYPE_WALLET_CARD_OPEN_CHARGE, FACT_WALLET_CARD_OPEN_CHARGE_IN_WALLET_SUM, entity.getId(), factMemo, factAmount);
    }

}
