package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JB2bDao;
import io.renren.zadmin.dto.JB2bDTO;
import io.renren.zadmin.entity.JB2bEntity;
import io.renren.zadmin.service.JB2bService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_b2b
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2025-04-23
 */
@Service
public class JB2bServiceImpl extends CrudServiceImpl<JB2bDao, JB2bEntity, JB2bDTO> implements JB2bService {

    @Override
    public QueryWrapper<JB2bEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JB2bEntity> wrapper = new QueryWrapper<>();


        return wrapper;
    }


}