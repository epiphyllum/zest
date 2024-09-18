package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dto.JMaccountDTO;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.service.JMaccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_maccount
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JMaccountServiceImpl extends CrudServiceImpl<JMaccountDao, JMaccountEntity, JMaccountDTO> implements JMaccountService {
    @Override
    public QueryWrapper<JMaccountEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JMaccountEntity> wrapper = new QueryWrapper<>();
        return wrapper;
    }
}