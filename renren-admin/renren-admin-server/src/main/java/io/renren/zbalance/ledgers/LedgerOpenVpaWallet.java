package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 批量开通-钱包卡
@Service
@Slf4j
public class LedgerOpenVpaWallet {

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private LedgerWalletCardOpenCharge ledgerWalletCardOpenCharge;

    // 冻结:批量-开通钱包子卡
    public void ledgeOpenVpaWalletFreeze(JVpaJobEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "冻结-批量开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
//        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_WALLET_CARD_OPEN, LedgerConstant.FACT_VPA_PREPAID_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        // 如果发行的是预付费子卡, 需要冻结预付费主卡总授权额度
        log.info("批量开卡, 冻结钱包账户....");
        ledgerWalletCardOpenCharge.ledgeWalletOpenChargeFreeze(entity);
    }

    // 解冻:批量-开通钱包子卡
    public void ledgeOpenVpaWalletUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "解冻-批量预付费卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_VPA_PREPAID_OPEN, LedgerConstant.FACT_VPA_PREPAID_OPEN_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        log.info("批量开卡, 解冻钱包账户....");
        ledgerWalletCardOpenCharge.ledgeWalletOpenChargeUnFreeze(entity);
    }

    // 确认VPA子卡开通
    public void ledgeOpenVpaWallet(JVpaJobEntity entity) {
        // 开卡费用
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-批量预防费卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();

        // 子商户va-费用确认
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_VPA_PREPAID_OPEN, LedgerConstant.FACT_VPA_PREPAID_OPEN_CONFIRM_SUB_VA, entity.getId(), factMemo, merchantFee);

        // 子商户-开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getCardFeeAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.ORIGIN_VPA_PREPAID_OPEN, LedgerConstant.FACT_VPA_PREPAID_OPEN_IN_SUB_FEE, entity.getId(), factMemo, merchantFee);

        // 预付费主卡额度
        log.info("预付费卡批量开卡, 确认主卡额度账户....");
        ledgerWalletCardOpenCharge.ledgeWalletOpenCharge(entity);
    }
}

