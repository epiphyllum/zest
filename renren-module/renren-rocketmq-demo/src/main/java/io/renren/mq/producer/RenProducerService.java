package io.renren.mq.producer;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
public class RenProducerService {
    @Value("${renren.rocketmq.orderTopic}")
    private String orderTopic;
    @Value("${renren.rocketmq.payTopic}")
    private String payTopic;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送订单消息
     */
    public SendResult sendOrder(String msgBody) {
        SendResult sendResult = rocketMQTemplate.syncSend(orderTopic, MessageBuilder.withPayload(msgBody).build());
        return sendResult;
    }

    /**
     * 发送支付信息
     */
    public void sendPay(String msgBody) {
        rocketMQTemplate.syncSend(payTopic, MessageBuilder.withPayload(msgBody).build());
    }
}