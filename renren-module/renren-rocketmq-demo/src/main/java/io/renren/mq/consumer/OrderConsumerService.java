package io.renren.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RocketMQMessageListener(topic = "${renren.rocketmq.orderTopic}", consumerGroup = "${renren.rocketmq.orderConsumer}")
public class OrderConsumerService implements RocketMQListener<String>{

    @Override
    public void onMessage(String str) {
        log.info("监听到消息：str={}", str);
    }

}