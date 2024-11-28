package io.renren.zmanager;

import io.renren.zadmin.dao.JPayChannelDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.entity.JSubEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JPayChannelManager {

    @Resource
    private JSubDao jSubDao;
    @Resource
    private JPayChannelDao jPayChannelDao;

    // 补充agentId, agentName, merchantName
    public void fillBySub(JPayChannelEntity entity) {
        Long subId = entity.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setSubName(subEntity.getCusname());
    }

    public void save(JPayChannelEntity entity) {
        fillBySub(entity);
        jPayChannelDao.insert(entity);
    }
}
