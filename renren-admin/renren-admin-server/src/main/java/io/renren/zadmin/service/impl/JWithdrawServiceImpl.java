package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.dto.JWithdrawDTO;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zadmin.service.JWithdrawService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_withdraw
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JWithdrawServiceImpl extends CrudServiceImpl<JWithdrawDao, JWithdrawEntity, JWithdrawDTO> implements JWithdrawService {

    @Override
    public QueryWrapper<JWithdrawEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JWithdrawEntity> wrapper = new QueryWrapper<>();

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
            wrapper.eq("sub_id", Long.parseLong(subId));
        }

        return wrapper;
    }


}