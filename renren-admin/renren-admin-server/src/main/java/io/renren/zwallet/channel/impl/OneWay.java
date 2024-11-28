package io.renren.zwallet.channel.impl;

import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zwallet.channel.PayChannel;

public class OneWay implements PayChannel {
    @Override
    public void init(JPayChannelEntity channelEntity) {

    }

    @Override
    public void charge(JWalletTxnEntity txnEntity) {

    }

    @Override
    public void chargeNotified(JWalletTxnEntity txnEntity) {

    }
}
