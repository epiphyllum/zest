package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.dto.JMoneyDTO;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zadmin.service.JMoneyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@Service
public class JMoneyServiceImpl extends CrudServiceImpl<JMoneyDao, JMoneyEntity, JMoneyDTO> implements JMoneyService {

    @Override
    public QueryWrapper<JMoneyEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMoneyEntity> wrapper = new QueryWrapper<>();

        String agentId = (String) params.get("agentId");
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq("agent_id", Long.parseLong(agentId));
        }

        //
        String merchantId = (String) params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId)) {
            wrapper.eq("merchant_id", Long.parseLong(merchantId));
        }

        UserDetail user = SecurityUser.getUser();
        if (agentId == null && "agent".equals(user.getUserType())) {
            wrapper.eq("agent_id", user.getDeptId());
        } else if (merchantId != null && "merchant".equals(user.getUserType())) {
            wrapper.eq("merchant_id", user.getDeptId());
        }


        //
        String acctno = (String) params.get("acctno");
        wrapper.like(StringUtils.isNotBlank(acctno), "acctno", acctno);

        //
        String payeraccountname = (String) params.get("payeraccountname");
        wrapper.like(StringUtils.isNotBlank(payeraccountname), "payeraccountname", payeraccountname);

        //
        String payeraccountno = (String) params.get("payeraccountno");
        wrapper.like(StringUtils.isNotBlank(payeraccountno), "payeraccountno", payeraccountno);

        //
        String merchantName = (String) params.get("merchantName");
        wrapper.like(StringUtils.isNotBlank(merchantName), "merchant_name", merchantName);

        return wrapper;
    }


}