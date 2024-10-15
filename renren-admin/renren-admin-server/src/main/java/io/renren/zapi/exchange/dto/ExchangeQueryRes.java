package io.renren.zapi.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeQueryRes {
    private String meraplid;      // 申请单流水
    private String applyid;       // 申请单号

    private BigDecimal amount; // 支付金额
    private BigDecimal stlamount; // 结算金额
    private String payerccy;      // 支付币种
    private String payeeccy;      // 结算币种

    private BigDecimal fee;       // 手续费
    private BigDecimal fxrate;  // 汇率

    private BigDecimal exfee;     // 执行手续费
    private BigDecimal exfxrate;  // 执行汇率

    private String state;         // 状态
}
