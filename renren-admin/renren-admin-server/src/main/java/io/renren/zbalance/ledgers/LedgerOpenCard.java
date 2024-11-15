package io.renren.zbalance.ledgers;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LedgerOpenCard {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 开卡冻结
    public void ledgeOpenCardFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        String factMemo = "冻结-开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();

        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (factAmount.compareTo(subVa.getBalance()) > 0) {
            throw new RenException("余额不足");
        }
        // 记账
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 卡开解冻
    public void ledgeOpenCardUnFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        String factMemo = "解冻-开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();

        // 记账
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 原始凭证: 开卡费用
    public void ledgeOpenCard(JCardEntity entity) {
        BigDecimal merchantFee = entity.getMerchantfee();
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-开卡费用:" + showMerchantFee;

        // 子商户va扣除费用
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_CONFIRM_SUB_VA, entity.getId(), factMemo, merchantFee);

        // 子商户开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getCardFeeAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_IN_CARD_FEE, entity.getId(), factMemo, merchantFee);

        // 通联开卡费用
        JBalanceEntity aipCardFee = ledgerUtil.getAipCardFeeAccount(0L, entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCardFee, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_IN_AIP_CARD_FEE, entity.getId(), factMemo, entity.getFee());
    }
}