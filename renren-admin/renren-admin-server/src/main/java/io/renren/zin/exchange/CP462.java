package io.renren.zin.exchange;

import io.renren.zadmin.dao.JFreeDao;
import io.renren.zadmin.entity.JFreeEntity;
import io.renren.zbalance.ledgers.Ledger901Free;
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
    private Ledger901Free ledger901Free;
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
            ledger901Free.ledgeFree(jFreeEntity);
        });
    }
}
