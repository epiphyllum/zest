package io.renren.zwallet.channel;

import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 支付渠道定义
public interface PayChannel {
    /**
     * 初始化
     */
    void setContext(ChannelContext context);

    /**
     * 充值
     *
     * @param txnEntity
     */
    void charge(JWalletTxnEntity txnEntity);

    /**
     * 充值回调
     */
    void chargeNotified(JWalletTxnEntity txnEntity, HttpServletResponse response);
}
