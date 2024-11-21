package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JFeeConfigDao;
import io.renren.zadmin.entity.JFeeConfigEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class JCommon {
    @Resource
    private JFeeConfigDao jFeeConfigDao;

    public JFeeConfigEntity getFeeConfig(Long merchantId, String marketproduct) {
        // 产品费用配置
        JFeeConfigEntity feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                .eq(JFeeConfigEntity::getMerchantId, merchantId)
                .eq(JFeeConfigEntity::getMarketproduct, marketproduct)
        );
        if (feeConfig == null) {
            feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                    .eq(JFeeConfigEntity::getMerchantId, 0L)
                    .eq(JFeeConfigEntity::getMarketproduct, marketproduct)
            );
        }
        if (feeConfig == null) {
            throw new RenException("没有配置");
        }

        return feeConfig;
    }
}
