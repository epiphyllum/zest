package io.renren.zmanager;

import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.entity.JWalletConfigEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JWalletConfigManager {

    @Resource
    private JSubDao jSubDao;
    @Resource
    private JWalletConfigDao jWalletConfigDao;

    // 补充agentId, agentName, merchantName
    public void fillBySub(JWalletConfigEntity entity) {
        Long subId = entity.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());
    }

    public void save(JWalletConfigEntity entity) {
        fillBySub(entity);
        jWalletConfigDao.insert(entity);
    }
}
