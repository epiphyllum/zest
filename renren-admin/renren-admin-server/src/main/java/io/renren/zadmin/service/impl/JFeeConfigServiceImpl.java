package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JFeeConfigDao;
import io.renren.zadmin.dto.JFeeConfigDTO;
import io.renren.zadmin.entity.JFeeConfigEntity;
import io.renren.zadmin.service.JFeeConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_fee_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-12
 */
@Service
public class JFeeConfigServiceImpl extends CrudServiceImpl<JFeeConfigDao, JFeeConfigEntity, JFeeConfigDTO> implements JFeeConfigService {

    @Override
    public QueryWrapper<JFeeConfigEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JFeeConfigEntity> wrapper = new QueryWrapper<>();

        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String marketproduct = (String)params.get("marketproduct");
        wrapper.eq(StringUtils.isNotBlank(marketproduct), "marketproduct", marketproduct);

        return wrapper;
    }


}