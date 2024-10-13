package io.renren.zapi.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

// 换汇申请
@Data
public class ExchangeReq {
    private String	meraplid;//	申请单流水
    private String	payeeccy;//	到账币种
    private String	payerccy;//	卖出币种
    private BigDecimal amount;//	金额
}
