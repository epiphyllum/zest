package io.renren.zwallet.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zwallet/channel/callback")
public class WalletChannelController {

    // 渠道回调
    @PostMapping("callback/{channelId}/{orderId}")
    public String callback(@RequestParam("channelId") Long channelId, @RequestParam("orderId") Long orderId) {
        return "OK";
    }

}
