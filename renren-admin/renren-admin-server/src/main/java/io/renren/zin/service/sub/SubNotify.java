package io.renren.zin.service.sub;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zin.service.sub.dto.TSubStatusNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SubNotify {
    @Resource
    private JMerchantDao jMerchantDao;

    public void handle(TSubStatusNotify notify) {
        if (notify.getState().equals("04") || notify.getState().equals("05")) {
            JMerchantEntity jMerchantEntity = jMerchantDao.selectOne(Wrappers.<JMerchantEntity>lambdaQuery()
                    .eq(JMerchantEntity::getCusid, notify.getCusid())
            );
            jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                    .set(JMerchantEntity::getState, notify.getState())
                    .eq(JMerchantEntity::getId, jMerchantEntity.getId())
            );
        }
    }
}
