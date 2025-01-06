package io.renren.zbalance.ledgers;

import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//
@Service
public class Ledger607WalletCommission {
    @Resource
    private LedgerUtil ledgerUtil;

    // 开卡返佣记账
    public static int ORIGIN_VPA_WALLET_OPEN_COMMISSION = 607;
    public static int FACT_VPA_WALLET_OPEN_COMMISSION_IN_WALLET = 60701;

    // 充值返佣记账
    public static int ORIGIN_VPA_WALLET_CHARGE_COMMISSION = 608;
    public static int FACT_VPA_WALLET_CHARGE_COMMISSION_IN_WALLET = 60801;

    // 发卡成功， 直接推荐人拿佣金
    public void ledgeOpenCommission1(JVpaJobEntity job, BigDecimal cardFee, JWalletEntity parent, JWalletConfigEntity config) {
        BigDecimal s1OpenRate = config.getS1OpenRate();
        BigDecimal s1OpenFee = s1OpenRate.multiply(cardFee).setScale(2, BigDecimal.ROUND_HALF_UP);
        String factMemo = "开卡佣金";
        JBalanceEntity parentBalance = ledgerUtil.getWalletAccount(parent.getId(), job.getProductcurrency());
        ledgerUtil.ledgeUpdate(parentBalance, ORIGIN_VPA_WALLET_OPEN_COMMISSION, FACT_VPA_WALLET_OPEN_COMMISSION_IN_WALLET, job.getId(), factMemo, s1OpenFee);
    }

    // 发卡成功， 间接接推荐人拿佣金
    public void ledgeOpenCommission2(JVpaJobEntity job, BigDecimal cardFee, JWalletEntity grand, JWalletConfigEntity config) {
        BigDecimal s2OpenRate = config.getS1OpenRate();
        BigDecimal s2OpenFee = s2OpenRate.multiply(cardFee).setScale(2, BigDecimal.ROUND_HALF_UP);
        JBalanceEntity grandBalance = ledgerUtil.getWalletAccount(grand.getId(), job.getProductcurrency());
        String factMemo = "开卡佣金";
        ledgerUtil.ledgeUpdate(grandBalance, ORIGIN_VPA_WALLET_OPEN_COMMISSION, FACT_VPA_WALLET_OPEN_COMMISSION_IN_WALLET, job.getId(), factMemo, s2OpenFee);
    }

    // 充值成功， 直接推荐人拿佣金
    public void ledgeChargeCommission1(JVpaJobEntity job, BigDecimal cardFee, JWalletEntity walletEntity, JWalletConfigEntity config) {
        BigDecimal s1ChargeRate = config.getS1ChargeRate();
        BigDecimal s1ChargeFee = s1ChargeRate.multiply(cardFee).setScale(2, BigDecimal.ROUND_HALF_UP);
        JBalanceEntity parent = ledgerUtil.getWalletAccount(walletEntity.getP1(), job.getProductcurrency());
        String factMemo = "充值佣金";
        ledgerUtil.ledgeUpdate(parent, ORIGIN_VPA_WALLET_CHARGE_COMMISSION, FACT_VPA_WALLET_CHARGE_COMMISSION_IN_WALLET, job.getId(), factMemo, s1ChargeFee);
    }

    // 充值成功， 间接推荐人拿佣金
    public void ledgeChargeCommission2(JVpaJobEntity job, BigDecimal cardFee, JWalletEntity walletEntity, JWalletConfigEntity config) {
        BigDecimal s2ChargeRate = config.getS1ChargeRate();
        BigDecimal s2ChargeFee = s2ChargeRate.multiply(cardFee).setScale(2, BigDecimal.ROUND_HALF_UP);
        JBalanceEntity parent = ledgerUtil.getWalletAccount(walletEntity.getP1(), job.getProductcurrency());
        String factMemo = "充值佣金";
        ledgerUtil.ledgeUpdate(parent, ORIGIN_VPA_WALLET_CHARGE_COMMISSION, FACT_VPA_WALLET_CHARGE_COMMISSION_IN_WALLET, job.getId(), factMemo, s2ChargeFee);
    }

}
