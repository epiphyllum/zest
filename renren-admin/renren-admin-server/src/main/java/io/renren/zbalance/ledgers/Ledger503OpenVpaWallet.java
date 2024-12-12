package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zadmin.entity.JWalletConfigEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 批量开通-钱包卡
@Service
@Slf4j
public class Ledger503OpenVpaWallet {
    public static int ORIGIN_VPA_WALLET_OPEN = 503;

    public static int FACT_VPA_WALLET_OPEN_FREEZE_SUB_VA = 50300;
    public static int FACT_VPA_WALLET_OPEN_UNFREEZE_SUB_VA = 50301;
    public static int FACT_VPA_WALLET_OPEN_CONFIRM_SUB_VA = 50302;
    public static int FACT_VPA_WALLET_OPEN_IN_SUB_FEE = 50304;

    // 替子商户记账
    public static int FACT_VPA_WALLET_OPEN_FREEZE_WALLET = 50305;
    public static int FACT_VPA_WALLET_OPEN_UNFREEZE_WALLET = 50306;
    public static int FACT_VPA_WALLET_OPEN_CONFIRM_WALLET = 50307;

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private Ledger604WalletCardOpenCharge ledger604WalletCardOpenCharge;
    @Resource
    private JWalletConfigDao jWalletConfigDao;

    // 冻结:批量-开通钱包子卡
    public void ledgeOpenVpaWalletFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());

        BigDecimal openCardFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "冻结-批量开卡费用:" + openCardFee;
        BigDecimal factAmount = entity.getMerchantfee();

        // 子商户va扣除费用冻结
        ledgerUtil.freezeUpdate(subVa, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        // 批量开卡: 主卡额度冻结
        // ledger604WalletCardOpenCharge.ledgeWalletOpenChargeFreeze(entity);

        // 替商户记录钱包用户的帐
        this.ledgeUserFreeze(entity);
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
        this.ledgeUserUnFreeze(entity);
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
        this.ledgeUser(entity);
    }

    ///////////////////////////
    private void ledgeUserFreeze(JVpaJobEntity entity) {
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
        BigDecimal cardFee = null;
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

    private void ledgeUserUnFreeze(JVpaJobEntity entity) {
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
                .multiply(new BigDecimal(entity.getNum())).setScale(2, RoundingMode.HALF_UP);
        // 充值手续费: 0 (因为是收款的时候就扣了充值手续费了)
        BigDecimal factAmount = principal.add(principal).add(cardFee);
        String factMemo = String.format("开通%d张卡, 总面值:%s, 开卡费:%s, 合计:%s", entity.getNum(), principal, cardFee, factAmount);
        ledgerUtil.unFreezeUpdate(wallet, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_UNFREEZE_WALLET, entity.getId(), factMemo, factAmount);
    }

    private void ledgeUser(JVpaJobEntity entity) {
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
                .multiply(new BigDecimal(entity.getNum())).setScale(2, RoundingMode.HALF_UP);

        BigDecimal factAmount = principal.add(principal).add(cardFee);
        String factMemo = String.format("开通%d张卡, 总面值:%s, 开卡费:%s, 合计:%s", entity.getNum(), principal, cardFee, factAmount);
        ledgerUtil.confirmUpdate(wallet, ORIGIN_VPA_WALLET_OPEN, FACT_VPA_WALLET_OPEN_CONFIRM_WALLET, entity.getId(), factMemo, factAmount);
    }
}

