package io.renren.zin.exchange;

import com.wechat.pay.java.service.payments.model.Transaction;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dao.JFreeDao;
import io.renren.zadmin.entity.JFreeEntity;
import io.renren.zbalance.LedgerUtil;
import io.renren.zbalance.ledgers.LedgerFree;
import io.renren.zin.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

// CP462  释放担保金
@Service
public class CP462 {
    @Resource
    private JFreeDao jFreeDao;
    @Resource
    private LedgerFree ledgerFree;
    @Resource
    private TransactionTemplate tx;

    // 释放担保金处理
    public void handle(TExchangeStateNotify notify) {
        JFreeEntity jFreeEntity = new JFreeEntity();
        jFreeEntity.setAmount(notify.getStlamount());
        jFreeEntity.setCurrency(notify.getStlccy());
        jFreeEntity.setApplyid(notify.getApplyid());
        tx.executeWithoutResult(st -> {
            jFreeDao.insert(jFreeEntity);
            ledgerFree.ledgeFree(jFreeEntity);
        });
    }
}
