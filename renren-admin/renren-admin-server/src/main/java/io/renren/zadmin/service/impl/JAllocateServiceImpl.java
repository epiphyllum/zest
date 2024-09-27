package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JAllocateDao;
import io.renren.zadmin.dto.JAllocateDTO;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.service.JAllocateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_inout
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Service
public class JAllocateServiceImpl extends CrudServiceImpl<JAllocateDao, JAllocateEntity, JAllocateDTO> implements JAllocateService {

    @Override
    public QueryWrapper<JAllocateEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JAllocateEntity> wrapper = new QueryWrapper<>();
        String deptId = (String)params.get("deptId");
        wrapper.eq(StringUtils.isNotBlank(deptId), "dept_id", deptId);
        return wrapper;
    }

}