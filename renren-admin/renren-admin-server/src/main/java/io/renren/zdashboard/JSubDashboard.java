package io.renren.zdashboard;

import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zmanager.JCardManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JSubDashboard {
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JCardManager jCardManager;
}