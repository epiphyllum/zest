package io.renren.zwallet.api;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zwallet.channel.ChannelFactory;
import io.renren.zwallet.channel.SwapChannel;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("zwallet/channel")
@Slf4j
public class WalletChannelController {

    @Resource
    private ChannelFactory channelFactory;
    @Resource
    private JWalletTxnDao jWalletTxnDao;

    // 渠道回调
    @PostMapping("callback/{subId}/{channelId}/{orderId}")
    public void chargeNotified(
            @PathVariable("subId") Long subId,
            @PathVariable("channelId") Long channelId,
            @PathVariable("orderId") Long orderId,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("收到支付回调, 子商户:{}, 渠道:{}, 订单号:{}", subId, channelId, orderId);
        SwapChannel channel = channelFactory.getChannel(channelId);
        JWalletTxnEntity jWalletTxnEntity = jWalletTxnDao.selectById(orderId);
        if (jWalletTxnEntity == null) {
            throw new RenException("回调非法, 订单号不能存在:" + orderId);
        }
        channel.swapNotified(jWalletTxnEntity,request, response);
    }

}
