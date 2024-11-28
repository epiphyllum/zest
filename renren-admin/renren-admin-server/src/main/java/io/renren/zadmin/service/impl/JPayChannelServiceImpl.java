package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.zadmin.dao.JPayChannelDao;
import io.renren.zadmin.dto.JPayChannelDTO;
import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.service.JPayChannelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_pay_channel
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Service
public class JPayChannelServiceImpl extends CrudServiceImpl<JPayChannelDao, JPayChannelEntity, JPayChannelDTO> implements JPayChannelService {

    @Override
    public QueryWrapper<JPayChannelEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JPayChannelEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);

        return wrapper;
    }


}