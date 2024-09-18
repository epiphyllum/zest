package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dto.JExchangeDTO;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.service.JExchangeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_exchange
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JExchangeServiceImpl extends CrudServiceImpl<JExchangeDao, JExchangeEntity, JExchangeDTO> implements JExchangeService {

    @Override
    public QueryWrapper<JExchangeEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JExchangeEntity> wrapper = new QueryWrapper<>();

        String deptId = (String)params.get("deptId");
        wrapper.eq(StringUtils.isNotBlank(deptId), "dept_id", deptId);

        return wrapper;
    }


}