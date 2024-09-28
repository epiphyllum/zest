package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dto.JAuthDTO;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.service.JAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JAuthServiceImpl extends CrudServiceImpl<JAuthDao, JAuthEntity, JAuthDTO> implements JAuthService {

    @Override
    public QueryWrapper<JAuthEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JAuthEntity> wrapper = new QueryWrapper<>();

        String agentId = (String) params.get("agentId");
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq("agent_id", Long.parseLong(agentId));
        }

        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId)) {
            wrapper.eq("merchant_id", Long.parseLong(merchantId));
        }

        String cardno = (String) params.get("cardno");
        wrapper.eq(StringUtils.isNotBlank(cardno), "cardno", cardno);
        String trxtype = (String) params.get("trxtype");
        wrapper.eq(StringUtils.isNotBlank(trxtype), "trxtype", trxtype);
        String authcode = (String) params.get("authcode");
        wrapper.eq(StringUtils.isNotBlank(authcode), "authcode", authcode);


        UserDetail user = SecurityUser.getUser();
        if ("agent".equals(user.getUserType())) {
            wrapper.eq("agent_id", user.getDeptId());
        } else if ("sub".equals(user.getUserType())) {
            wrapper.eq("sub_id", user.getDeptId());
        } else if ("merchant".equals(user.getUserType())) {
            wrapper.eq("merchant_id", user.getDeptId());
        }

        return wrapper;
    }


}