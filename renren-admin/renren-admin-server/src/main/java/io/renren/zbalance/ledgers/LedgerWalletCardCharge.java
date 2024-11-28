package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 钱包子卡单卡充值
@Service
@Slf4j
public class LedgerWalletCardCharge {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 钱包子卡-单笔充值(调整钱包余额)
    public void ledgeWalletCardChargeFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(
                Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-单笔充值:%s", factAmount);
        // 记账
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.freezeUpdate(walletAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_CHARGE, LedgerConstant.FACT_WALLET_CARD_CHARGE_FREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    //钱包子卡 单笔充值解冻(调整主卡可用额度)
    public void ledgeWalletCardChargeUnFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("解冻-单笔充值:%s", factAmount);
        // 记账
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.unFreezeUpdate(walletAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_CHARGE, LedgerConstant.FACT_WALLET_CARD_CHARGE_UNFREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    //钱包子卡 单笔开卡充值确认(调整主卡可用额度)
    public void ledgeWalletCardCharge(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, maincardno));
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("确认-单笔充值:%s", factAmount);
        // 钱包余额 - 确认
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.confirmUpdate(walletAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_CHARGE, LedgerConstant.FACT_WALLET_CARD_CHARGE_CONFIRM_WALLET, entity.getId(), factMemo, factAmount);
        // 发卡总额 - 确认
        JBalanceEntity walletSumAccount = ledgerUtil.getWalletSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(walletSumAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_CHARGE, LedgerConstant.FACT_WALLET_CARD_CHARGE_IN_WALLET_SUM, entity.getId(), factMemo, factAmount);
    }

}
