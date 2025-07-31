package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JBatchDao;
import io.renren.zadmin.dto.JBatchDTO;
import io.renren.zadmin.entity.JBatchEntity;
import io.renren.zadmin.service.JBatchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_batch
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-19
 */
@Service
public class JBatchServiceImpl extends CrudServiceImpl<JBatchDao, JBatchEntity, JBatchDTO> implements JBatchService {

    @Override
    public QueryWrapper<JBatchEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JBatchEntity> wrapper = new QueryWrapper<>();

        String batchType = (String)params.get("batchType");
        wrapper.eq(StringUtils.isNotBlank(batchType), "batch_type", batchType);

        String batchDate = (String)params.get("batchDate");
        wrapper.eq(StringUtils.isNotBlank(batchDate), "batch_date", batchDate);

        wrapper.orderByDesc("batch_date", "batch_type");

        return wrapper;
    }


}