package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
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
    public QueryWrapper<JExchangeEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JExchangeEntity> wrapper = new QueryWrapper<>();

        String agentId = (String) params.get("agentId");
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq("agent_id", Long.parseLong(agentId));
        }

        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId)) {
            wrapper.eq("merchant_id", Long.parseLong(merchantId));
        }

        UserDetail user = SecurityUser.getUser();
        if (agentId == null && "agent".equals(user.getUserType())) {
            wrapper.eq("agent_id", user.getDeptId());
        } else if (merchantId == null && "merchant".equals(user.getUserType())) {
            wrapper.eq("merchant_id", user.getDeptId());
        }


        return wrapper;
    }


}