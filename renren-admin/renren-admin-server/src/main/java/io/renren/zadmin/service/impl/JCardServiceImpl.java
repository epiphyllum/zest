package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.service.JCardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Service
public class JCardServiceImpl extends CrudServiceImpl<JCardDao, JCardEntity, JCardDTO> implements JCardService {

    @Override
    public QueryWrapper<JCardEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JCardEntity> wrapper = new QueryWrapper<>();


        return wrapper;
    }


}