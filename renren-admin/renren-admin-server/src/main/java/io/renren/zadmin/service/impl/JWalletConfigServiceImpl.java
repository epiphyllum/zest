package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.dto.JWalletConfigDTO;
import io.renren.zadmin.entity.JWalletConfigEntity;
import io.renren.zadmin.service.JWalletConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_wallet_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Service
public class JWalletConfigServiceImpl extends CrudServiceImpl<JWalletConfigDao, JWalletConfigEntity, JWalletConfigDTO> implements JWalletConfigService {

    @Override
    public QueryWrapper<JWalletConfigEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JWalletConfigEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);
        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String subId = (String)params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);

        return wrapper;
    }


}