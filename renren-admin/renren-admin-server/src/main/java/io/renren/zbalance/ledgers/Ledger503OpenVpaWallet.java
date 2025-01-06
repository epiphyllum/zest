package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerUtil;
import io.renren.zwallet.ZWalletConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 批量开通-钱包卡
@Service
@Slf4j
public class Ledger503OpenVpaWallet {
    // 钱包批量开卡
    public static int ORIGIN_VPA_WALLET_OPEN = 503;
    public static int FACT_VPA_WALLET_OPEN_FREEZE_SUB_VA = 50300;
    public static int FACT_VPA_WALLET_OPEN_UNFREEZE_SUB_VA = 50301;
    public static int FACT_VPA_WALLET_OPEN_CONFIRM_SUB_VA = 50302;
    public static int FACT_VPA_WALLET_OPEN_IN_SUB_FEE = 50304;
    // 钱包批量开
    public static int FACT_VPA_WALLET_OPEN_FREEZE_WALLET = 50305;
    public static int FACT_VPA_WALLET_OPEN_UNFREEZE_WALLET = 50306;
    public static int FACT_VPA_WALLET_OPEN_CONFIRM_WALLET = 50307;

    // 开卡佣金记账
    @Resource
    private Ledger607WalletCommission ledger607WalletOpenCommission;
    // 开卡充值记账
    @Resource
    private Ledger608WalletChargeCommission ledger608WalletChargeCommission;

    @Resource
    private JWalletDao jWalletDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private Ledger604WalletCardOpenCharge ledger604WalletCardOpenCharge;
    @Resource
    private JWalletConfigDao jWalletConfigDao;
    @Resource
    private JWalletTxnDao jWalletTxnDao;

    // 冻结:批量-开通钱包子卡
    public void ledgeOpenVpaWalletFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());

        BigDecimal openCardFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "冻结-批量开卡费用:" + openCardFee;
        BigDecimal factAmount = entity.getMerchantfee();

        // 子商户va扣除费用冻结
        ledgerUtil.freezeUpdate(subVa, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        // 替商户记录钱包用户的帐
        this.ledgeWalletFreeze(entity);
    }

    // 解冻:批量-开通钱包子卡
    public void ledgeOpenVpaWalletUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "解冻-批量预付费卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        log.info("批量开卡, 主卡额度解冻....");
        // ledger604WalletCardOpenCharge.ledgeWalletOpenChargeUnFreeze(entity);

        // 子商户用户记账
        this.ledgeWalletUnFreeze(entity);
    }

    // 确认: 钱包子卡开通成功
    public void ledgeOpenVpaWallet(JVpaJobEntity entity) {
        // 开卡费用
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-批量钱包卡开卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();

        // 子商户va-费用确认
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.confirmUpdate(subVa, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_CONFIRM_SUB_VA, entity.getId(), factMemo, merchantFee);

        // 子商户-开卡费用确认
        JBalanceEntity feeAccount = ledgerUtil.getCardFeeAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.ledgeUpdate(feeAccount, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_IN_SUB_FEE, entity.getId(), factMemo, merchantFee);

        // 钱包主卡-发卡额更新
        ledger604WalletCardOpenCharge.ledgeWalletOpenCharge(entity);

        // 子商户发卡数量
        JBalanceEntity cardCount = ledgerUtil.getCardCountAccount(entity.getSubId(), entity.getProductcurrency());
        factMemo = "批量开" + entity.getNum() + "张卡";
        ledgerUtil.ledgeUpdate(cardCount, Ledger500OpenCard.ORIGIN_CARD_OPEN,
                Ledger500OpenCard.FACT_CARD_OPEN_IN_CARD_COUNT, entity.getId(), factMemo, new BigDecimal(entity.getNum()));

        // 子商户用户记账
        this.ledgeWallet(entity);
    }

    ///////////////////////////
    // 钱包记账
    ///////////////////////////
    private void ledgeWalletFreeze(JVpaJobEntity entity) {
        JBalanceEntity wallet = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getProductcurrency());

        // 替子商户记账: 冻结钱包账户: 开卡收费 + 每张卡本身充值费用
        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, entity.getSubId())
        );

        // 用户本金
        BigDecimal principal = entity.getAuthmaxamount()
                .multiply(new BigDecimal(entity.getNum()))
                .setScale(2, RoundingMode.HALF_UP);

        // 用户开卡费
        BigDecimal cardFee;
        if (entity.getProductcurrency().equals("HKD")) {
            cardFee = jWalletConfigEntity.getVpaOpenFee()
                    .multiply(new BigDecimal(entity.getNum()))
                    .multiply(jWalletConfigEntity.getHkdRate())
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (entity.getProductcurrency().equals("USD")) {
            cardFee = jWalletConfigEntity.getVpaOpenFee()
                    .multiply(new BigDecimal(entity.getNum()))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new RenException("不支持的币种:" + entity.getProductcurrency());
        }

        // 充值手续费: 0 (因为是收款的时候就扣了充值手续费了)
        BigDecimal factAmount = principal.add(cardFee);

        // 钱包余额不足
        if (wallet.getBalance().compareTo(factAmount) < 0) {
            throw new RenException("余额不足");
        }
        String factMemo = String.format("开通%d张卡, 总面值:%s, 总开卡费:%s, 合计:%s", entity.getNum(), principal, cardFee, factAmount);
        ledgerUtil.freezeUpdate(wallet, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_FREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    private void ledgeWalletUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity wallet = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getProductcurrency());
        // 替子商户记账: 冻结钱包账户: 开卡收费 + 每张卡本身充值费用
        JWalletConfigEntity jWalletConfigEntity = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, entity.getSubId())
        );
        // 用户本金
        BigDecimal principal = entity.getAuthmaxamount()
                .multiply(new BigDecimal(entity.getNum()))
                .setScale(2, RoundingMode.HALF_UP);
        // 用户开卡费
        BigDecimal cardFee = jWalletConfigEntity.getVccOpenFee()
                .multiply(new BigDecimal(entity.getNum()))
                .setScale(2, RoundingMode.HALF_UP);
        // 充值手续费: 0 (因为是收款的时候就扣了充值手续费了)
        BigDecimal factAmount = principal.add(principal).add(cardFee);
        String factMemo = String.format("开通%d张卡, 总面值:%s, 开卡费:%s, 合计:%s", entity.getNum(), principal, cardFee, factAmount);
        ledgerUtil.unFreezeUpdate(wallet, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_UNFREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    private void ledgeWallet(JVpaJobEntity entity) {
        JBalanceEntity wallet = ledgerUtil.getWalletAccount(entity.getWalletId(), entity.getProductcurrency());
        // 替子商户记账: 冻结钱包账户: 开卡收费 + 每张卡本身充值费用
        JWalletConfigEntity walletConfig = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, entity.getSubId())
        );

        // 记账: 扣除用户余额: 开卡费 + 充值费
        BigDecimal principal = entity.getAuthmaxamount()
                .multiply(new BigDecimal(entity.getNum()))
                .setScale(2, RoundingMode.HALF_UP); // 用户本金
        BigDecimal cardFee = walletConfig.getVpaOpenFee()
                .multiply(new BigDecimal(entity.getNum())).setScale(2, RoundingMode.HALF_UP); // 用户开卡费
        BigDecimal factAmount = principal.add(principal).add(cardFee);
        String factMemo = String.format("开通%d张卡, 总面值:%s, 开卡费:%s, 合计:%s", entity.getNum(), principal, cardFee, factAmount);
        ledgerUtil.confirmUpdate(wallet, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_CONFIRM_WALLET, entity.getId(), factMemo, factAmount);

        ledgeWalletCommission(entity, walletConfig, principal, cardFee);
    }

    // 用户返佣记账
    private void ledgeWalletCommission(JVpaJobEntity entity, JWalletConfigEntity walletConfig, BigDecimal principal, BigDecimal cardFee) {

        JWalletEntity walletEntity = jWalletDao.selectById(entity.getWalletId());
        JWalletEntity parent = jWalletDao.selectById(walletEntity.getP1());

        // 开卡返佣
        ledger607WalletOpenCommission.ledgeOpenCommission1(entity, cardFee, walletEntity, walletConfig);

        // 更新统计
        jWalletDao.update(null, Wrappers.<JWalletEntity>lambdaUpdate()
                .eq(JWalletEntity::getId, walletEntity.getP1())
                .eq(JWalletEntity::getVersion, parent.getVersion())
                .set(JWalletEntity::getVersion, parent.getVersion() + 1)
                .set(entity.getCurrency().equals("HKD"), JWalletEntity::getS1OpenFeeHkd, s1OpenFee.add(parent.getS1OpenFeeHkd()))
                .set(entity.getCurrency().equals("USD"), JWalletEntity::getS1OpenFeeUsd, s1OpenFee.add(parent.getS1OpenFeeUsd()))
                .set(entity.getCurrency().equals("HKD"), JWalletEntity::getS1ChargeFeeHkd, s1ChargeFee.add(parent.getS1ChargeFeeHkd()))
                .set(entity.getCurrency().equals("USD"), JWalletEntity::getS1ChargeFeeUsd, s1ChargeFee.add(parent.getS1ChargeFeeUsd()))
        );

        // 记账: 间接推荐人
        if (walletEntity.getP2() != null) {
            // 二级开卡返佣
            JWalletEntity grandParent = jWalletDao.selectById(walletEntity.getP2());
            BigDecimal s2OpenFee = s2OpenRate.multiply(cardFee).setScale(2, RoundingMode.HALF_UP);
            JWalletTxnEntity txnEntityOpen2 = new JWalletTxnEntity();
            fillTxn(txnEntityOpen2, walletEntity);
            txnEntityOpen2.setTxnCode(ZWalletConstant.WALLET_TXN_COMMISSION_OPEN);
            txnEntityOpen2.setFee(s2OpenFee);
            txnEntityOpen2.setState(ZWalletConstant.WALLET_TXN_STATUS_SUCCESS);
            txnEntityOpen2.setFromCurrency(entity.getProductcurrency());
            txnEntityOpen2.setToCurrency(entity.getProductcurrency());

            // 二级充值返佣
            BigDecimal s2ChargeFee = principal.multiply(chargeRate).multiply(s2ChargeRate).setScale(2, RoundingMode.HALF_UP);
            JWalletTxnEntity txnEntityCharge2 = new JWalletTxnEntity();
            fillTxn(txnEntityCharge2, walletEntity);
            txnEntityCharge2.setTxnCode(ZWalletConstant.WALLET_TXN_COMMISSION_CHARGE);
            txnEntityCharge2.setFee(s2ChargeFee);
            txnEntityCharge2.setFromCurrency(entity.getProductcurrency());
            txnEntityCharge2.setToCurrency(entity.getProductcurrency());
            txnEntityCharge2.setState(ZWalletConstant.WALLET_TXN_STATUS_SUCCESS);

            // 开卡返佣
            jWalletTxnDao.insert(txnEntityOpen2);
            ledger607WalletOpenCommission.ledgeOpenCommission(txnEntityOpen2);

            // 充值返佣
            jWalletTxnDao.insert(txnEntityCharge2);
            ledger608WalletChargeCommission.ledgeChargeCommission(txnEntityCharge2);

            jWalletDao.update(null, Wrappers.<JWalletEntity>lambdaUpdate()
                    .eq(JWalletEntity::getId, grandParent.getId())
                    .eq(JWalletEntity::getVersion, grandParent.getVersion())
                    .set(JWalletEntity::getVersion, grandParent.getVersion() + 1)
                    .set(entity.getCurrency().equals("HKD"), JWalletEntity::getS2OpenFeeHkd, s2OpenFee.add(grandParent.getS2OpenFeeHkd()))
                    .set(entity.getCurrency().equals("USD"), JWalletEntity::getS2OpenFeeUsd, s2OpenFee.add(grandParent.getS2OpenFeeUsd()))
                    .set(entity.getCurrency().equals("HKD"), JWalletEntity::getS2ChargeFeeHkd, s2ChargeFee.add(grandParent.getS2ChargeFeeHkd()))
                    .set(entity.getCurrency().equals("USD"), JWalletEntity::getS2ChargeFeeUsd, s2ChargeFee.add(grandParent.getS2ChargeFeeUsd()))
            );
        }
    }

    // 钱包填充交易
    private void fillTxn(JWalletTxnEntity txnEntity, JWalletEntity walletEntity) {
        txnEntity.setWalletId(walletEntity.getId());
        txnEntity.setWalletName(walletEntity.getEmail());
        txnEntity.setAgentId(walletEntity.getAgentId());
        txnEntity.setAgentName(walletEntity.getAgentName());
        txnEntity.setMerchantId(walletEntity.getMerchantId());
        txnEntity.setMerchantName(walletEntity.getMerchantName());
        txnEntity.setSubId(walletEntity.getSubId());
        txnEntity.setSubName(walletEntity.getSubName());
    }

}

