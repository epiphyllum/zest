package io.renren.zwallet.channel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.entity.JWalletConfigEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zbalance.ledgers.Ledger610WalletCharge;
import io.renren.zcommon.ZinConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;

// 支付渠道定义
public interface PayChannel {
    /**
     * 初始化
     */
    void setContext(ChannelContext context);

    ChannelContext getContext();

    void setConfig(JPayChannelEntity channelEntity);

    JPayChannelEntity getConfig();

    /**
     * 充值
     *
     * @param txnEntity
     */
    String charge(JWalletTxnEntity txnEntity);

    default String getCallbackUrl(JWalletTxnEntity txnEntity) {
        ChannelContext context = getContext();
        JPayChannelEntity channelEntity = getConfig();

        JWalletConfigDao jWalletConfigDao = context.getJWalletConfigDao();
        JWalletConfigEntity config = jWalletConfigDao.selectOne(Wrappers.<JWalletConfigEntity>lambdaQuery()
                .eq(JWalletConfigEntity::getSubId, txnEntity.getSubId())
        );
        // 回调 /sys/zwallet/channel/callback/:subId/:channelId/:orderId
        String callbackUrl = config.getProtocol() + "://" + config.getDomain() +
                "/sys/zwallet/channel/callback/" + txnEntity.getSubId() +
                "/" + channelEntity.getId() +
                "/" + txnEntity.getId();
        return callbackUrl;
    }

    /**
     * 充值回调
     */
    default void chargeNotified(JWalletTxnEntity txnEntity, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ChannelContext context = getContext();
        TransactionTemplate tx = context.getTx();
        JWalletTxnDao jWalletTxnDao = context.getJWalletTxnDao();
        Ledger610WalletCharge ledger610WalletCharge = context.getLedger610WalletCharge();
        tx.executeWithoutResult(status -> {
            ledger610WalletCharge.ledgeWalletCharge(txnEntity);
            jWalletTxnDao.update(null, Wrappers.<JWalletTxnEntity>lambdaUpdate()
                    .set(JWalletTxnEntity::getState, ZinConstant.WALLET_TXN_STATUS_SUCCESS)
                    .eq(JWalletTxnEntity::getId, txnEntity.getId())
            );
        });
        response.getWriter().print(response());
    }

    default String response() {
        return "OK";
    }

}
