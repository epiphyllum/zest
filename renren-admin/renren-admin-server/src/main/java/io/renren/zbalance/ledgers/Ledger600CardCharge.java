package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class Ledger600CardCharge {
    // 卡充值
    public static final int ORIGIN_TYPE_CARD_CHARGE = 600;                    // 卡充值
    public static final int FACT_CARD_CHARGE_FREEZE_SUB_VA = 60000;           // 0. 冻结va资金
    public static final int FACT_CARD_CHARGE_UNFREEZE_SUB_VA = 60001;         // 1. 解冻va资金
    public static final int FACT_CARD_CHARGE_CONFIRM_SUB_VA = 60002;          // 2. 确认va资金
    public static final int FACT_CARD_CHARGE_IN_CARD_SUM = 60003;             // 3. 子商户-卡汇总充值
    public static final int FACT_CARD_CHARGE_IN_DEPOSIT = 60004;              // 4. 子商户-保证金收取
    public static final int FACT_CARD_CHARGE_IN_CHARGE = 60005;               // 5. 子商户-保证金收取
    public static final int FACT_CARD_CHARGE_IN_AIP_DEPOSIT = 60006;          // 6. 通联累计保证金
    public static final int FACT_CARD_CHARGE_IN_AIP_CHARGE = 60007;           // 7. 通联累计充值手续费
    public static final int FACT_CARD_CHARGE_IN_AIP_CARD_SUM = 60008;         // 8. 通联累计手续费
    public static final int FACT_CARD_CHARGE_IN_PREPAID_QUOTA = 60009;         // 9. 如果是预付费卡: 需要入金预付费主卡账户
    public static final int FACT_CARD_CHARGE_IN_WALLET_QUOTA = 60010;          // 10. 如果是钱包主卡: 需要入金

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    //  vcc与vpa主卡充值:
    public void ledgeCardChargeFreeze(JDepositEntity entity, JSubEntity sub) {
        // 交易金额 + 商户充值手续费 + 充值保证金
        BigDecimal factAmount = entity.getAmount()
                .add(entity.getMerchantCharge())
                .add(entity.getMerchantDeposit());

        String factMemo = String.format("冻结-卡充值:%s, 到账:%s, 保证金:%s, 手续费:%s",
                factAmount,
                entity.getAmount(), entity.getMerchantDeposit(), entity.getMerchantCharge());

        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());

        // 子商户VA冻结
        ledgerUtil.freezeUpdate(subVa, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 取消卡充值, 卡充值失败
    public void ledgeCardChargeUnFreeze(JDepositEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount()
                .add(entity.getMerchantCharge())
                .add(entity.getMerchantDeposit());
        String factMemo = String.format("解冻-卡充值:%s, 到账:%s, 保证金:%s, 手续费:%s",
                factAmount,
                entity.getAmount(), entity.getMerchantDeposit(), entity.getMerchantCharge());
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());

        // 子商户va取消冻结
        ledgerUtil.unFreezeUpdate(subVa, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 卡充值
    public void ledgeCardCharge(JDepositEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount()
                .add(entity.getMerchantCharge())
                .add(entity.getMerchantDeposit());
        String factMemo = String.format("确认-卡充值:%s, 到账:%s, 保证金:%s, 手续费:%s",
                factAmount,
                entity.getAmount(), entity.getMerchantDeposit(), entity.getMerchantCharge());

        ///////////////////////////////////////////////////////////////////////////////////
        // 记账1: 子商户VA确认冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(subVa, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_CONFIRM_SUB_VA, entity.getId(), factMemo, factAmount);

        // 记账2: 子商户-发卡总额
        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal cardSumAmount = entity.getAmount();
        ledgerUtil.ledgeUpdate(cardSum, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_CARD_SUM, entity.getId(), factMemo, cardSumAmount);

        // 记账3: 子商户-保证金
        JBalanceEntity deposit = ledgerUtil.getDepositAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(deposit, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_DEPOSIT, entity.getId(), factMemo, entity.getMerchantDeposit());

        // 记账4: 子商户-手续费
        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(charge, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_CHARGE, entity.getId(), factMemo, entity.getMerchantCharge());

        ///////////////////////////////////////////////////////////////////////////////////
        // 通联-累计充值金额
        JBalanceEntity aipCardSum = ledgerUtil.getAipCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCardSum, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_AIP_CARD_SUM, entity.getId(), factMemo, entity.getTxnAmount());

        // 通联-累计保证金额
        JBalanceEntity depositSum = ledgerUtil.getAipDepositAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(depositSum, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_AIP_DEPOSIT, entity.getId(), factMemo, entity.getSecurityamount());

        // 通联-累计手续费金额
        JBalanceEntity aipCharge = ledgerUtil.getAipChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCharge, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_AIP_CHARGE, entity.getId(), factMemo, entity.getFee());

        // 预付费主卡充值:
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity prepaidQuota = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.ledgeUpdate(prepaidQuota, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_PREPAID_QUOTA, entity.getId(), factMemo, entity.getAmount());
        }

//        // 钱包主卡充值
//        else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_WALLET)) {
//            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
//                    .eq(JCardEntity::getCardno, entity.getCardno())
//            );
//            JBalanceEntity walletQuota = ledgerUtil.getWalletQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
//            ledgerUtil.ledgeUpdate(walletQuota, ORIGIN_TYPE_CARD_CHARGE, FACT_CARD_CHARGE_IN_WALLET_QUOTA, entity.getId(), factMemo, entity.getAmount());
//        }

    }
}