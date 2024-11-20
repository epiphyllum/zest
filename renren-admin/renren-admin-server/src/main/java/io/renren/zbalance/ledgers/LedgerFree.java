package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JFreeEntity;
import io.renren.zadmin.entity.JMfreeEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
@Slf4j
public class LedgerFree {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 通联释放担保金
    public void ledgeFree(JFreeEntity entity) {
        // ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_MFREE, LedgerConstant.FACT_MFREE_IN, entity.getId(), factMemo, entity.getAmount());
    }
}
