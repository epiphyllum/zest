package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JFreeDao;
import io.renren.zadmin.dto.JFreeDTO;
import io.renren.zadmin.entity.JFreeEntity;
import io.renren.zadmin.service.JFreeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_free
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-21
 */
@Service
public class JFreeServiceImpl extends CrudServiceImpl<JFreeDao, JFreeEntity, JFreeDTO> implements JFreeService {

    @Override
    public QueryWrapper<JFreeEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JFreeEntity> wrapper = new QueryWrapper<>();

        String applyid = (String)params.get("applyid");
        wrapper.eq(StringUtils.isNotBlank(applyid), "applyid", applyid);

        return wrapper;
    }


}