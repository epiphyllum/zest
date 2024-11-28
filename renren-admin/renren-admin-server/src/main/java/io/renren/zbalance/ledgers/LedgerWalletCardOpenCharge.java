package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LedgerWalletCardOpenCharge {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    //钱包子卡-批量开卡充值(调整主卡可以额度)
    public void ledgeWalletOpenChargeFreeze(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("冻结-批量开通%s卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);

        // 记账
        JBalanceEntity walletBalance = ledgerUtil.getWalletAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.freezeUpdate(walletBalance, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_OPEN_CHARGE, LedgerConstant.FACT_WALLET_CARD_OPEN_CHARGE_FREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    //钱包子卡 批量充值解冻(调整主卡可用额度)
    public void ledgeWalletOpenChargeUnFreeze(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getWalletQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("解冻-批量开通%s张卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_OPEN_CHARGE, LedgerConstant.FACT_WALLET_CARD_OPEN_CHARGE_UNFREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    //钱包子卡 批量开卡充值确认(调整主卡可用额度)
    public void ledgeWalletOpenCharge(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("确认-批量开通%s张卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);

        // 记账1: 钱包-
        JBalanceEntity prepaidBalance = ledgerUtil.getWalletQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.confirmUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_OPEN_CHARGE, LedgerConstant.FACT_WALLET_CARD_OPEN_CHARGE_CONFIRM_WALLET, entity.getId(), factMemo, factAmount);

        // 记账2: 钱包子卡 发卡总额
        JBalanceEntity prepaidSumBalance = ledgerUtil.getWalletSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(prepaidSumBalance, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_OPEN_CHARGE, LedgerConstant.FACT_WALLET_CARD_OPEN_CHARGE_IN_WALLET_SUM, entity.getId(), factMemo, factAmount);
    }
}
