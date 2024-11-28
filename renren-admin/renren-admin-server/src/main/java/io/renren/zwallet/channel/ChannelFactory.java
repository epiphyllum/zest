package io.renren.zwallet.channel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JPayChannelDao;
import io.renren.zadmin.entity.JPayChannelEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ChannelFactory {

    @Resource
    private JPayChannelDao jPayChannelDao;

    public PayChannel getChannel(String channelName, Long subId) {
        JPayChannelEntity jPayChannelEntity = jPayChannelDao.selectOne(Wrappers.<JPayChannelEntity>lambdaQuery()
                .eq(JPayChannelEntity::getChannelName, channelName)
                .eq(JPayChannelEntity::getSubId, subId)
        );
        return null;
    }
}
