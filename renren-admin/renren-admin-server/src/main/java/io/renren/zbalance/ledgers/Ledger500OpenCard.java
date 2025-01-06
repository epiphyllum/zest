package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JWalletConfigEntity;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Wrapper;

@Service
@Slf4j
public class Ledger500OpenCard {
    // 开卡
    public static final int ORIGIN_CARD_OPEN = 500;                   // 开卡
    public static final int FACT_CARD_OPEN_FREEZE_SUB_VA = 50000;     // 0. 子商户va冻结
    public static final int FACT_CARD_OPEN_UNFREEZE_SUB_VA = 50001;   // 1. 子商户va解冻
    public static final int FACT_CARD_OPEN_CONFIRM_SUB_VA = 50002;    // 2. 子商户va确认
    public static final int FACT_CARD_OPEN_IN_CARD_FEE = 50003;       // 3. 子商户-开卡费用账户
    public static final int FACT_CARD_OPEN_IN_AIP_CARD_FEE = 50004;   // 4. 通联开卡费
    // 这个是多个地方共用的
    public static final int FACT_CARD_OPEN_IN_CARD_COUNT = 5005;      // 5. 开卡统计
    // 钱包记账
    public static final int FACT_CARD_OPEN_FREEZE_WALLET = 5006;      // 6.
    public static final int FACT_CARD_OPEN_UNFREEZE_WALLET = 5007;    // 7.
    public static final int FACT_CARD_OPEN_CONFIRM_WALLET = 5008;     // 8.

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JWalletConfigDao jWalletConfigDao;

    //
    public void upgradeWalletFreeze(JCardEntity entity) {
        String factMemo;
        BigDecimal factAmount = entity.getMerchantfee();
        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery().eq(JWalletConfigEntity::getSubId, entity.getSubId()));
        BigDecimal upgradeFee = jWalletConfigEntity.getUpgradeFee();
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());
        if (entity.getCurrency().equals("USD")) {
            factMemo = "美元账户升级, 收费:" + upgradeFee + "USD";
            ledgerUtil.freezeUpdate(walletAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_FREEZE_WALLET, entity.getId(), factMemo, upgradeFee);
        }
        else if (entity.getCurrency().equals("HKD")) {
            BigDecimal hkdRate = jWalletConfigEntity.getHkdRate();
            upgradeFee = upgradeFee.multiply(hkdRate).setScale(2, RoundingMode.HALF_UP);
            factMemo = "港币账户升级, 收费:" + factAmount + "HKD";
            ledgerUtil.freezeUpdate(walletAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_FREEZE_WALLET, entity.getId(), factMemo, upgradeFee);
        }
    }

    //
    public void upgradeWalletUnfreeze(JCardEntity entity) {
        String factMemo;
        BigDecimal factAmount = entity.getMerchantfee();
        // 钱包用户-账户升级-开卡:  收取升级费用
        if (entity.getWalletId() != null && entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_WALLET)) {
            JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery().eq(JWalletConfigEntity::getSubId, entity.getSubId()));
            BigDecimal upgradeFee = jWalletConfigEntity.getUpgradeFee();
            JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());
            if (entity.getCurrency().equals("USD")) {
                factMemo = "美元账户升级, 收费:" + factAmount + "USD";
                ledgerUtil.unFreezeUpdate(walletAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_UNFREEZE_WALLET, entity.getId(), factMemo, factAmount);
            }
            else if (entity.getCurrency().equals("HKD")) {
                BigDecimal hkdRate = jWalletConfigEntity.getHkdRate();
                upgradeFee = upgradeFee.multiply(hkdRate).setScale(2, RoundingMode.HALF_UP);
                factMemo = "港币账户升级, 收费:" + factAmount + "HKD";
                ledgerUtil.unFreezeUpdate(walletAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_UNFREEZE_WALLET, entity.getId(), factMemo, upgradeFee);
            }
        }
    }

    // 升级钱包账户
    public void upgradeWallet(JCardEntity entity) {
        String factMemo;
        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery().eq(JWalletConfigEntity::getSubId, entity.getSubId()));
        BigDecimal upgradeFee = jWalletConfigEntity.getUpgradeFee();
        JBalanceEntity walletAccount = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getCurrency());
        if (entity.getCurrency().equals("USD")) {
            factMemo = "美元账户升级, 收费:" + upgradeFee + "USD";
            ledgerUtil.confirmUpdate(walletAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_CONFIRM_WALLET, entity.getId(), factMemo, upgradeFee);
        }
        else if (entity.getCurrency().equals("HKD")) {
            BigDecimal hkdRate = jWalletConfigEntity.getHkdRate();
            upgradeFee = upgradeFee.multiply(hkdRate).setScale(2, RoundingMode.HALF_UP);
            factMemo = "港币账户升级, 收费:" + upgradeFee + "HKD";
            ledgerUtil.confirmUpdate(walletAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_CONFIRM_WALLET, entity.getId(), factMemo, upgradeFee);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 开卡冻结
    public void ledgeOpenCardFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        String factMemo = "冻结-开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();

        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (factAmount.compareTo(subVa.getBalance()) > 0) {
            throw new RenException("余额不足, 余额:" + subVa.getBalance());
        }

        // 记账
        ledgerUtil.freezeUpdate(subVa, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        // 钱包用户-账户升级-开卡:  收取升级费用
        if (entity.getWalletId() != null && entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_WALLET)) {
            upgradeWalletFreeze(entity);
        }

    }

    // 卡开解冻
    public void ledgeOpenCardUnFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        String factMemo = "解冻-开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();

        // 记账
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(subVa, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        // 取消
        if (entity.getWalletId() != null && entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_WALLET)) {
            upgradeWalletUnfreeze(entity);
        }
    }

    // 原始凭证: 开卡费用
    public void ledgeOpenCard(JCardEntity entity) {
        BigDecimal merchantFee = entity.getMerchantfee();
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-开卡费用:" + showMerchantFee;

        // 子商户va扣除费用
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(subVa, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_CONFIRM_SUB_VA, entity.getId(), factMemo, merchantFee);

        // 子商户开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getCardFeeAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(feeAccount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_IN_CARD_FEE, entity.getId(), factMemo, merchantFee);

        // 通联开卡费用
        JBalanceEntity aipCardFee = ledgerUtil.getAipCardFeeAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCardFee, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_IN_AIP_CARD_FEE, entity.getId(), factMemo, entity.getFee());

        // 子商户发卡数量增加
        JBalanceEntity cardCount = ledgerUtil.getCardCountAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(cardCount, ORIGIN_CARD_OPEN, FACT_CARD_OPEN_IN_CARD_COUNT, entity.getId(), "开卡1张", BigDecimal.ONE);

        // 钱包用户-账户升级-开卡:  收取升级费用
        if (entity.getWalletId() != null && entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_WALLET)) {
            upgradeWallet(entity);
        }
    }
}