package io.renren.zwallet.channel.impl;

import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zwallet.channel.AbstractSwapChannel;
import io.renren.zwallet.channel.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class LocalUSDT extends AbstractSwapChannel {
    @Override
    public String swap(JWalletTxnEntity txnEntity) {
        ChannelContext context = getContext();
        RestTemplate restTemplate = context.getRestTemplate();

        String callbackUrl = getCallbackUrl(txnEntity);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                log.info("模拟支付回调:");
                HttpEntity<String> request = new HttpEntity<>("test get");
                restTemplate.postForEntity(callbackUrl, request, String.class);
            }
        }, 2000);
        return "usdt://";
    }

    @Override
    public String response() {
        return "SUCCESS";
    }
}
