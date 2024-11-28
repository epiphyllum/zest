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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 钱包充值
@Service
public class LedgerWalletCharge {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private LedgerWalletCardOpenCharge ledgerWalletCardOpenCharge;

    // 钱包-充值
    public void ledgeWalletChargeFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(
                Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-钱包充值:%s", factAmount);
        // 记账
        JBalanceEntity walletQuotaAccount = ledgerUtil.getWalletQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.freezeUpdate(walletQuotaAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CHARGE, LedgerConstant.FACT_WALLET_CHARGE_FREEZE_WALLET_QUOTA, entity.getId(), factMemo, factAmount);
    }

    // 钱包-充值
    public void ledgeWalletChargeUnfreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(
                Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-钱包充值:%s", factAmount);
        // 记账
        JBalanceEntity walletQuotaAccount = ledgerUtil.getWalletQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.unFreezeUpdate(walletQuotaAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CHARGE, LedgerConstant.FACT_WALLET_CHARGE_UNFREEZE_WALLET_QUOTA, entity.getId(), factMemo, factAmount);
    }

    // 钱包-充值
    public void ledgeWalletChargeConfirm(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(
                Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-钱包充值:%s", factAmount);

        // 记账: 主卡额度-
        JBalanceEntity walletQuotaAccount = ledgerUtil.getWalletQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.confirmUpdate(walletQuotaAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CHARGE, LedgerConstant.FACT_WALLET_CHARGE_CONFIRM_WALLET_QUOTA, entity.getId(), factMemo, factAmount);

        // 记账: 钱包+
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(null, cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(walletAccount, LedgerConstant.ORIGIN_TYPE_WALLET_CHARGE, LedgerConstant.FACT_WALLET_CHARGE_IN_WALLET, entity.getId(), factMemo, factAmount);
    }
}
