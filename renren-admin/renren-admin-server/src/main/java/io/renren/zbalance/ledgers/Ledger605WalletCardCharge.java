package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 钱包子卡单卡充值
@Service
@Slf4j
public class Ledger605WalletCardCharge {
    // 钱包子卡-单卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_WALLET_CARD_CHARGE = 605;                 // 钱包子卡-卡充值
    public static final int FACT_WALLET_CARD_CHARGE_FREEZE_WALLET = 60500;        // 0. 钱包 冻结
    public static final int FACT_WALLET_CARD_CHARGE_UNFREEZE_WALLET = 60501;      // 1. 钱包主卡额度 解结.
    public static final int FACT_WALLET_CARD_CHARGE_CONFIRM_WALLET = 60502;       // 2. 钱包主卡额度 确认
    public static final int FACT_WALLET_CARD_CHARGE_IN_WALLET_SUM = 60503;        // 3. 钱包子卡发卡总额

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 钱包子卡-单笔充值(冻结钱包余额)
    public void ledgeWalletCardChargeFreeze(JVpaAdjustEntity entity) {

        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-单笔充值:%s", factAmount);

        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());

        // 冻结钱包
        ledgerUtil.freezeUpdate(walletAccount, ORIGIN_TYPE_WALLET_CARD_CHARGE, FACT_WALLET_CARD_CHARGE_FREEZE_WALLET, entity.getId(), factMemo, factAmount);

    }

    //钱包子卡-单笔充值解冻(解冻钱包余额)
    public void ledgeWalletCardChargeUnFreeze(JVpaAdjustEntity entity) {
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("解冻-单笔充值:%s", factAmount);
        // 记账
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());

        // 解冻钱包
        ledgerUtil.unFreezeUpdate(walletAccount, ORIGIN_TYPE_WALLET_CARD_CHARGE, FACT_WALLET_CARD_CHARGE_UNFREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    // 钱包子卡-单笔充值确认
    public void ledgeWalletCardCharge(JVpaAdjustEntity entity) {

        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("确认-单笔充值:%s", factAmount);

        // 用户钱包
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());
        // 主卡发卡额
        JBalanceEntity walletSumAccount = ledgerUtil.getWalletSumAccount(entity.getMaincardid(), entity.getCurrency());

        // 钱包余额 - 确认
        ledgerUtil.confirmUpdate(walletAccount, ORIGIN_TYPE_WALLET_CARD_CHARGE, FACT_WALLET_CARD_CHARGE_CONFIRM_WALLET, entity.getId(), factMemo, factAmount);

        // 发卡总额 - 增加
        ledgerUtil.ledgeUpdate(walletSumAccount, ORIGIN_TYPE_WALLET_CARD_CHARGE, FACT_WALLET_CARD_CHARGE_IN_WALLET_SUM, entity.getId(), factMemo, factAmount);
    }

}
