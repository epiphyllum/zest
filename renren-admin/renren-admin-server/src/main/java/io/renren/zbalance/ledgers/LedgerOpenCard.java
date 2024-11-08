package io.renren.zbalance.ledgers;

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
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = "冻结开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡开解冻
    public void ledgeOpenCardUnFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = "解冻开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 原始凭证: 开卡费用
    public void ledgeOpenCard(JCardEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getCurrency());
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认开卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();
        // 子商户va扣除费用
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_CONFIRM, entity.getId(), factMemo, merchantFee);
        // 子商户开卡费用账户
        ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_FEE_IN, entity.getId(), factMemo, merchantFee);
    }
}