package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dto.JDepositDTO;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.service.JDepositService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JDepositServiceImpl extends CrudServiceImpl<JDepositDao, JDepositEntity, JDepositDTO> implements JDepositService {

    @Override
    public QueryWrapper<JDepositEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JDepositEntity> wrapper = new QueryWrapper<>();

        String agentId = (String) params.get("agentId");
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq("agent_id", Long.parseLong(agentId));
        }

        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId)) {
            wrapper.eq("merchant_id", Long.parseLong(merchantId));
        }

        String subId = (String) params.get("subId");
        if (StringUtils.isNotBlank(subId)) {
            wrapper.eq("sub_id", Long.parseLong(merchantId));
        }

        return wrapper;
    }

}