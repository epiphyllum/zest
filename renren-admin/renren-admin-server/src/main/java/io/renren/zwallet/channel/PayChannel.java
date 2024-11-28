package io.renren.zwallet.channel;

import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;

// 支付渠道定义
public interface PayChannel {
    /**
     * 初始化
     * @param channelEntity
     */
    void init(JPayChannelEntity channelEntity);

    /**
     * 充值
     * @param txnEntity
     */
    void charge(JWalletTxnEntity txnEntity);

    /**
     * 充值回调
     */
    void chargeNotified(JWalletTxnEntity txnEntity);
}
