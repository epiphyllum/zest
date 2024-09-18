package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JInoutDao;
import io.renren.zadmin.dto.JInoutDTO;
import io.renren.zadmin.entity.JInoutEntity;
import io.renren.zadmin.service.JInoutService;
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
public class JInoutServiceImpl extends CrudServiceImpl<JInoutDao, JInoutEntity, JInoutDTO> implements JInoutService {

    @Override
    public QueryWrapper<JInoutEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JInoutEntity> wrapper = new QueryWrapper<>();
        String deptId = (String)params.get("deptId");
        wrapper.eq(StringUtils.isNotBlank(deptId), "dept_id", deptId);
        return wrapper;
    }

}