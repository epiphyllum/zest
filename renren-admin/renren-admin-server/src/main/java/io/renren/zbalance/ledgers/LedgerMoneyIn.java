package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LedgerMoneyIn {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 原始凭证(100):  收到商户入金
    public void ledgeMoneyIn(JMoneyEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = "入金:" + entity.getAmount() + entity.getCurrency();
        ledgerUtil.ledgeUpdate(vaAccount, LedgerConstant.ORIGIN_TYPE_MONEY, LedgerConstant.FACT_MONEY_IN_VA, entity.getId(), factMemo, entity.getAmount());
    }
}
