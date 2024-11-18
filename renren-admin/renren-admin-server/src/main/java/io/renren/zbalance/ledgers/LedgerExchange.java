package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LedgerExchange {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 换汇冻结
    public void ledgeExchangeFreeze(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        JBalanceEntity outBalance = ledgerUtil.getVaAccount(merchantId, entity.getPayerccy());
        String factMemo = "冻结-换汇" + entity.getAmount() + entity.getPayerccy();
        BigDecimal factAmount = entity.getAmount();
        ledgerUtil.freezeUpdate(outBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_FREEZE_VA, entity.getId(), factMemo, factAmount);
    }

    // 换汇解冻
    public void ledgeExchangeUnFreeze(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        JBalanceEntity outBalance = ledgerUtil.getVaAccount(merchantId, entity.getPayerccy());
        String factMemo = "解冻-换汇" + entity.getAmount() + entity.getPayerccy();
        BigDecimal factAmount = entity.getAmount();
        ledgerUtil.unFreezeUpdate(outBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_UNFREEZE_VA, entity.getId(), factMemo, factAmount);
    }

    // 原始凭证: 换汇
    public void ledgeExchange(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        String factMemo = "确认-换汇" + entity.getAmount() + entity.getPayerccy() + ", 买入: " + entity.getSettleamount() + entity.getSettlecurrency();
        BigDecimal factAmount = entity.getSettleamount();
        // 卖出币种: 确认冻结
        JBalanceEntity outBalance = ledgerUtil.getVaAccount(merchantId, entity.getPayerccy());
        ledgerUtil.confirmUpdate(outBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_CONFIRM_VA, entity.getId(), factMemo, entity.getAmount());

        // 买入币种: 入账
        JBalanceEntity targetVa = ledgerUtil.getVaAccount(merchantId, entity.getPayeeccy());
        ledgerUtil.ledgeUpdate(targetVa, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_IN_VA, entity.getId(), factMemo, factAmount);
    }

}