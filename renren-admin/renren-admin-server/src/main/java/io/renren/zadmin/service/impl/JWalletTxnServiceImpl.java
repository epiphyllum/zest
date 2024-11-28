package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.dto.JWalletTxnDTO;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zadmin.service.JWalletTxnService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_wallet_txn
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Service
public class JWalletTxnServiceImpl extends CrudServiceImpl<JWalletTxnDao, JWalletTxnEntity, JWalletTxnDTO> implements JWalletTxnService {

    @Override
    public QueryWrapper<JWalletTxnEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JWalletTxnEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);
        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String subId = (String)params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);
        String maincardno = (String)params.get("maincardno");
        wrapper.eq(StringUtils.isNotBlank(maincardno), "maincardno", maincardno);
        String channelId = (String)params.get("channelId");
        wrapper.eq(StringUtils.isNotBlank(channelId), "channel_id", channelId);

        return wrapper;
    }


}