package io.renren.zin.sub;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zmanager.JMerchantManager;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zin.sub.dto.TSubStatusNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ZinSubNotifyService {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JMerchantManager jMerchantManager;

    // 子商户创建审核通知: 5002
    public void handle(TSubStatusNotify notify) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectOne(Wrappers.<JMerchantEntity>lambdaQuery()
                .eq(JMerchantEntity::getCusid, notify.getCusid())
        );
        if (jMerchantEntity == null) {
            log.error("找不到商户cusid:{}", notify.getCusid());
            throw new RenException("can not find merchant");
        }
        jMerchantManager.query(jMerchantEntity);
    }
}
