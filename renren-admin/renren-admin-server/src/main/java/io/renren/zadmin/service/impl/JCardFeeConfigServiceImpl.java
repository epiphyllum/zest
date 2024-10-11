package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JCardFeeConfigDao;
import io.renren.zadmin.dto.JCardFeeConfigDTO;
import io.renren.zadmin.entity.JCardFeeConfigEntity;
import io.renren.zadmin.service.JCardFeeConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_card_fee_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-10
 */
@Service
public class JCardFeeConfigServiceImpl extends CrudServiceImpl<JCardFeeConfigDao, JCardFeeConfigEntity, JCardFeeConfigDTO> implements JCardFeeConfigService {

    @Override
    public QueryWrapper<JCardFeeConfigEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JCardFeeConfigEntity> wrapper = new QueryWrapper<>();


        return wrapper;
    }


}