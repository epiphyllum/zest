package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class Ledger502OpenVpaPrepaid {

    // vpa预付费卡开卡
    public static final int ORIGIN_VPA_PREPAID_OPEN = 502;                    // vpa预付费子卡开卡
    public static final int FACT_VPA_PREPAID_FREEZE_SUB_VA = 50200;           // 1. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_UNFREEZE_SUB_VA = 50201;    // 2. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_CONFIRM_SUB_VA = 50202;     // 3. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_IN_SUB_FEE = 50203;         // 4. 子商户va账户 冻结
    public static final int FACT_VPA_PREPAID_OPEN_IN_AIP_CARD_FEE = 50204;    // 5. 通联开卡成本 - 0

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private Ledger601PrepaidOpenCharge ledger601PrepaidOpenCharge;

    // 共享子卡卡费
    public void ledgeOpenVpaPrepaidFreeze(JVpaJobEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "冻结-批量预付费卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.freezeUpdate(subVa, ORIGIN_VPA_PREPAID_OPEN, FACT_VPA_PREPAID_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        // 如果发行的是预付费子卡, 需要冻结预付费主卡总授权额度
        log.info("预付费卡批量开卡, 冻结主卡额度账户....");
        ledger601PrepaidOpenCharge.ledgePrepaidOpenChargeFreeze(entity);
    }

    // 解冻VPA子卡开通
    public void ledgeOpenVpaPrepaidUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "解冻-批量预付费卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, ORIGIN_VPA_PREPAID_OPEN, FACT_VPA_PREPAID_OPEN_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);

        log.info("预付费卡批量开卡, 解冻主卡额度账户....");
        ledger601PrepaidOpenCharge.ledgePrepaidOpenChargeUnFreeze(entity);
    }

    // 确认VPA子卡开通
    public void ledgeOpenVpaPrepaid(JVpaJobEntity entity) {
        // 开卡费用
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-批量预防费卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();

        // 子商户va-费用确认
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.confirmUpdate(subVa, ORIGIN_VPA_PREPAID_OPEN, FACT_VPA_PREPAID_OPEN_CONFIRM_SUB_VA, entity.getId(), factMemo, merchantFee);

        // 子商户-开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getCardFeeAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.ledgeUpdate(feeAccount, ORIGIN_VPA_PREPAID_OPEN, FACT_VPA_PREPAID_OPEN_IN_SUB_FEE, entity.getId(), factMemo, merchantFee);

        // 预付费主卡额度
        log.info("预付费卡批量开卡, 确认主卡额度账户....");
        ledger601PrepaidOpenCharge.ledgePrepaidOpenCharge(entity);

        // 子商户发卡数量增加
        JBalanceEntity cardCount = ledgerUtil.getCardCountAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(cardCount, Ledger500OpenCard.ORIGIN_CARD_OPEN,
                Ledger500OpenCard.FACT_CARD_OPEN_IN_CARD_COUNT, entity.getId(), "批量开" + entity.getNum() + "张卡", new BigDecimal(entity.getNum()));

    }
}

