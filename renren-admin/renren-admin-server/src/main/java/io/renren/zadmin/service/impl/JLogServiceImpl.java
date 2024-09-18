package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JLogDao;
import io.renren.zadmin.dto.JLogDTO;
import io.renren.zadmin.entity.JLogEntity;
import io.renren.zadmin.service.JLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Service
public class JLogServiceImpl extends CrudServiceImpl<JLogDao, JLogEntity, JLogDTO> implements JLogService {

    @Override
    public QueryWrapper<JLogEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JLogEntity> wrapper = new QueryWrapper<>();

        String deptId = (String)params.get("deptId");
        wrapper.eq(StringUtils.isNotBlank(deptId), "dept_id", deptId);
        String balanceType = (String)params.get("balanceType");
        wrapper.eq(StringUtils.isNotBlank(balanceType), "balance_type", balanceType);
        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);
        String factType = (String)params.get("factType");
        wrapper.eq(StringUtils.isNotBlank(factType), "fact_type", factType);
        String factId = (String)params.get("factId");
        wrapper.eq(StringUtils.isNotBlank(factId), "fact_id", factId);

        return wrapper;
    }


}