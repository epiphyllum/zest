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

// 钱包子卡-单卡提现
@Service
@Slf4j
public class Ledger606WalletCardWithdraw {

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 钱包子卡-提现: 操作对应主卡
    public static final int ORIGIN_TYPE_WALLET_CARD_WITHDRAW = 606;           // 钱包子卡-卡提现
    public static final int FACT_WALLET_CARD_WITHDRAW_IN_WALLET = 60600;      // 1: 调增钱包剩余额度
    public static final int FACT_WALLET_CARD_WITHDRAW_OUT_WALLET_SUM = 60601; // 2: 发卡总额减

    //钱包子卡 单笔提现(调整主卡可用额度)
    public void ledgeWalletCardWithdraw(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount().negate();
        String factMemo = String.format("钱包子卡提现:%s", factAmount);

        // 钱包
        JBalanceEntity walletBalance = ledgerUtil.getWalletAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(walletBalance, ORIGIN_TYPE_WALLET_CARD_WITHDRAW, FACT_WALLET_CARD_WITHDRAW_IN_WALLET, entity.getId(), factMemo, factAmount);

        // 钱包主卡-发卡总额
        JBalanceEntity walletSum = ledgerUtil.getWalletSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(walletSum, ORIGIN_TYPE_WALLET_CARD_WITHDRAW, FACT_WALLET_CARD_WITHDRAW_OUT_WALLET_SUM, entity.getId(), factMemo, factAmount.negate());
    }
}
