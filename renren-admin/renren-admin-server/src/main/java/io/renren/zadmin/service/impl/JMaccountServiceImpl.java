package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dto.JMaccountDTO;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.service.JMaccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_maccount
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JMaccountServiceImpl extends CrudServiceImpl<JMaccountDao, JMaccountEntity, JMaccountDTO> implements JMaccountService {
    @Override
    public QueryWrapper<JMaccountEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JMaccountEntity> wrapper = new QueryWrapper<>();

//        String agentId = (String) params.get("agentId");
//        if (StringUtils.isNotBlank(agentId)) {
//            wrapper.eq("agent_id", Long.parseLong(agentId));
//        }
//        String merchantId = (String) params.get("merchantId");
//        if (StringUtils.isNotBlank(merchantId)) {
//            wrapper.eq("merchant_id", Long.parseLong(merchantId));
//        }
//        UserDetail user = SecurityUser.getUser();
//        if (agentId != null && ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
//            // 代理访问
//            wrapper.eq("agent_id", user.getDeptId());
//        } else if (merchantId == null && ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
//            // 商户访问
//            wrapper.eq("merchant_id", user.getDeptId());
//        }

        CommonFilter.setFilterMerchant(wrapper, params);

        return wrapper;
    }
}