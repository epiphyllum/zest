package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JConfigDao;
import io.renren.zadmin.dto.JConfigDTO;
import io.renren.zadmin.entity.JConfigEntity;
import io.renren.zadmin.service.JConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-09
 */
@Service
public class JConfigServiceImpl extends CrudServiceImpl<JConfigDao, JConfigEntity, JConfigDTO> implements JConfigService {

    @Override
    public QueryWrapper<JConfigEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JConfigEntity> wrapper = new QueryWrapper<>();


        return wrapper;
    }


}