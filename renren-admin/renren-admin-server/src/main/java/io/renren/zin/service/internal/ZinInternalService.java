package io.renren.zin.service.internal;

import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinRequester;
import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

public class ZinInternalService {

    @Resource
    private ZinRequester requester;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JExchangeDao jExchangeDao;

    @Resource
    private TransactionTemplate tx;
    @Resource
    private Ledger ledger;
    @Resource
    private ZestConfig zestConfig;
}
