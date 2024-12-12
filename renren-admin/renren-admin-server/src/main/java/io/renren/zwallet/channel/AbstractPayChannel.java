package io.renren.zwallet.channel;

import io.renren.zadmin.entity.JPayChannelEntity;

public abstract class AbstractPayChannel implements PayChannel {
    private ChannelContext context;
    private JPayChannelEntity channelEntity;

    @Override
    public void setContext(ChannelContext channelContext) {
        this.context = channelContext;
    }

    @Override
    public ChannelContext getContext() {
        return context;
    }

    @Override
    public JPayChannelEntity getConfig() {
        return channelEntity;
    }

    @Override
    public void setConfig(JPayChannelEntity payChannelEntity) {
        this.channelEntity = payChannelEntity;
    }
}
