package io.renren.mq.controller;

import io.renren.mq.producer.RenProducerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("rocketmq")
public class RocketMQController {
    private final RenProducerService renProducerService;

    @GetMapping("order")
    public String order() {
        renProducerService.sendOrder("订单001");

        return "success";
    }

    @GetMapping("pay")
    public String pay() {
        renProducerService.sendPay("支付001");

        return "success";
    }
}
