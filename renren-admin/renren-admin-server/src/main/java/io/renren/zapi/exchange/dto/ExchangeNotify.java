package io.renren.zapi.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

// 我这里只做换汇通知等我们商户
@Data
public class ExchangeNotify {
    private String meraplid;      // 申请单流水
    private String applyid;       // 申请单号

    private BigDecimal amount;    // 支付金额
    private BigDecimal stlamount; // 结算金额
    private String payerccy;      // 支付币种
    private String payeeccy;      // 结算币种

    private BigDecimal fee;       // 手续费
    private BigDecimal fxrate;    // 汇率

    private BigDecimal exfee;     // 执行手续费
    private BigDecimal exfxrate;  // 执行汇率

    private String ps;            // 附言
    private String purpose;       // 汇款用途
    private String remark;        // 备注
    private String state;         // 状态
}
