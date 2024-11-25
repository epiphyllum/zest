package io.renren.zmanager;

import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMfreeDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMfreeEntity;
import io.renren.zbalance.ledgers.LedgerMfree;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

// 释放商户担保金
@Service
public class JMfreeManager {
    @Resource
    private JMfreeDao jMfreeDao;
    @Resource
    private LedgerMfree ledgerMfree;
    @Resource
    private JMerchantDao jMerchantDao;

    @Resource
    private TransactionTemplate tx;

    public void save(JMfreeEntity entity) {
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setMerchantName(merchant.getCusname());
        entity.setStatDate(new Date());
        tx.executeWithoutResult(st -> {
            jMfreeDao.insert(entity);
            ledgerMfree.ledgeMfree(entity);
        });
    }
}
