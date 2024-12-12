package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JFeeConfigDao;
import io.renren.zadmin.entity.JFeeConfigEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JCommon {
    @Resource
    private JFeeConfigDao jFeeConfigDao;

    // 产品成本与收入配置
    public JFeeConfigEntity getFeeConfig(Long merchantId, String marketproduct, String currency) {
        // 产品费用配置
        JFeeConfigEntity feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                .eq(JFeeConfigEntity::getMerchantId, merchantId)
                .eq(JFeeConfigEntity::getMarketproduct, marketproduct)
                .eq(JFeeConfigEntity::getCurrency, currency)
        );
        if (feeConfig == null) {
            feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                    .eq(JFeeConfigEntity::getMerchantId, 0L)
                    .eq(JFeeConfigEntity::getMarketproduct, marketproduct)
                    .eq(JFeeConfigEntity::getCurrency, currency)
            );
        }
        if (feeConfig == null) {
            log.error("缺乏配置{}-{}-{}", marketproduct, currency, merchantId);
            throw new RenException("没有配置");
        }

        return feeConfig;
    }
}
