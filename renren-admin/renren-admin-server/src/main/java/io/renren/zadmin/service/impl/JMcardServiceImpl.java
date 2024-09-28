package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.service.JMcardService;
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
public class JMcardServiceImpl extends CrudServiceImpl<JMcardDao, JMcardEntity, JMcardDTO> implements JMcardService {

    @Override
    public QueryWrapper<JMcardEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMcardEntity> wrapper = new QueryWrapper<>();

        String agentId = (String) params.get("agentId");
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", Long.parseLong(agentId));
        }

        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId)) {
            wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", Long.parseLong(merchantId));
        }

        UserDetail user = SecurityUser.getUser();
        if ("agent".equals(user.getUserType())) {
            wrapper.eq("agent_id", user.getDeptId());
        } else if ("merchant".equals(user.getUserType())) {
            wrapper.eq("merchant_id", user.getDeptId());
        }

        return wrapper;
    }

}