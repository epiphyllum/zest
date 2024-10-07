package io.renren.zin.service.sub;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.manager.JMerchantManager;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zin.service.sub.dto.TSubStatusNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ZinSubNotifyService {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JMerchantManager manager;

    // 子商户创建审核通知: 5002
    public void handle(TSubStatusNotify notify) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectOne(Wrappers.<JMerchantEntity>lambdaQuery()
                .eq(JMerchantEntity::getCusid, notify.getCusid())
        );
        if (jMerchantEntity == null) {
            throw new RenException("can not find merchant");
        }
        manager.changeState(jMerchantEntity, notify.getState(), notify.getCusid());
    }
}
