package io.renren.zwallet.channel;

import io.renren.zadmin.entity.JPayChannelEntity;
import lombok.Data;

@Data
public class ChannelContext {
    JPayChannelEntity channelEntity;
    public ChannelContext(JPayChannelEntity channelEntity) {
        this.channelEntity = channelEntity;
    }
}
