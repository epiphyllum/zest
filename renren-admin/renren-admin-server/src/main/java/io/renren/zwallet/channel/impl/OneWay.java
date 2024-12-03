package io.renren.zwallet.channel.impl;

import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zwallet.channel.ChannelContext;
import io.renren.zwallet.channel.PayChannel;
import jakarta.servlet.http.HttpServletResponse;

public class OneWay implements PayChannel {
    private ChannelContext context;

    @Override
    public void setContext(ChannelContext channelContext) {
        this.context = channelContext;
    }

    @Override
    public void charge(JWalletTxnEntity txnEntity) {
    }

    @Override
    public void chargeNotified(JWalletTxnEntity txnEntity, HttpServletResponse response) {
    }
}
