package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JStatDao;
import io.renren.zadmin.dto.JStatDTO;
import io.renren.zadmin.entity.JStatEntity;
import io.renren.zadmin.service.JStatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_stat
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Service
public class JStatServiceImpl extends CrudServiceImpl<JStatDao, JStatEntity, JStatDTO> implements JStatService {

    @Override
    public QueryWrapper<JStatEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JStatEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);
        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String subId = (String)params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);
        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);
        String marketproduct = (String)params.get("marketproduct");
        wrapper.eq(StringUtils.isNotBlank(marketproduct), "marketproduct", marketproduct);
        String statDate = (String)params.get("statDate");
        wrapper.eq(StringUtils.isNotBlank(statDate), "stat_date", statDate);

        return wrapper;
    }


}