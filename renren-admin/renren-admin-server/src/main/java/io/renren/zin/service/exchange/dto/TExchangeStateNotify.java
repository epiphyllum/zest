package io.renren.zin.service.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TExchangeStateNotify {
    private String meraplid;      // 申请单流水
    private String acctno;        // 受益人账号
    private String lockflg;       // 锁定方

    private BigDecimal payamount; // 支付金额
    private String payccy;        // 支付币种
    private BigDecimal stlamount; // 结算金额
    private String stlccy;        // 结算币种

    private BigDecimal fee;       // 手续费
    private String feeccy;    // 手续费
    private BigDecimal fxrate;    // 汇率

    private String ps;            // 附言
    private String purpose;       // 汇款用途

    private String applyid;       // 申请单号
    private String state;         // 状态
    private String stateexplain;  // 状态描述

    private String trxcode;       // 交易类型
    private String time;          // 通知时间
}
