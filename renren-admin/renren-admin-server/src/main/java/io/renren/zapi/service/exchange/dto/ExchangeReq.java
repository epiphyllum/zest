package io.renren.zapi.service.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeReq {
    private String	meraplid;//	申请单流水
    private String	payeeccy;//	到账币种
    private String	payerccy;//	卖出币种
    private BigDecimal amount;//	金额
}
