package io.renren.zwallet.api;

import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zwallet.channel.ChannelFactory;
import io.renren.zwallet.channel.PayChannel;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zwallet/channel/callback")
public class WalletChannelController {

    @Resource
    private ChannelFactory channelFactory;
    @Resource
    private JWalletTxnDao jWalletTxnDao;

    // 渠道回调
    @PostMapping("callback/{channelId}/{orderId}")
    public void chargeNotified(
            @RequestParam("channelId") Long channelId,
            @RequestParam("orderId") Long orderId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        PayChannel channel = channelFactory.getChannel(channelId);
        JWalletTxnEntity jWalletTxnEntity = jWalletTxnDao.selectById(orderId);
        channel.chargeNotified(jWalletTxnEntity,response);
        return;
    }

}
