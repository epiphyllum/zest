package io.renren.zmanager;

import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMfreeDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMfreeEntity;
import io.renren.zbalance.Ledger;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class JMfreeManager {

    @Resource
    private JMfreeDao jMfreeDao;

    @Resource
    private Ledger ledger;

    @Resource
    private JMerchantDao jMerchantDao;

    @Resource
    private TransactionTemplate tx;

    public void save(JMfreeEntity entity) {
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setMerchantName(merchant.getCusname());
        tx.executeWithoutResult(st -> {
            jMfreeDao.insert(entity);
            ledger.ledgeMfree(entity);
        });
    }
}
