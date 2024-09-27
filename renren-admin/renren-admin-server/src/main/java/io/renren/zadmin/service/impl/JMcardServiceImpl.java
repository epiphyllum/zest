package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.service.JMcardService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Service
public class JMcardServiceImpl extends CrudServiceImpl<JMcardDao, JMcardEntity, JMcardDTO> implements JMcardService {

    @Override
    public QueryWrapper<JMcardEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMcardEntity> wrapper = new QueryWrapper<>();
        return wrapper;
    }

}