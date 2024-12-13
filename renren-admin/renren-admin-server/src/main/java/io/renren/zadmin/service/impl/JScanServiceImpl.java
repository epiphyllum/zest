package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JScanDao;
import io.renren.zadmin.dto.JScanDTO;
import io.renren.zadmin.entity.JScanEntity;
import io.renren.zadmin.service.JScanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_tron
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-12-13
 */
@Service
public class JScanServiceImpl extends CrudServiceImpl<JScanDao, JScanEntity, JScanDTO> implements JScanService {

    @Override
    public QueryWrapper<JScanEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JScanEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);
        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String subId = (String)params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);
        String walletId = (String)params.get("walletId");
        wrapper.eq(StringUtils.isNotBlank(walletId), "wallet_id", walletId);
        String txid = (String)params.get("txid");
        wrapper.eq(StringUtils.isNotBlank(txid), "txid", txid);

        return wrapper;
    }


}