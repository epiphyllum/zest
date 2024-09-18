package io.renren.zin.service.exchange.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TExchangeQueryResponse extends TResult {
    private String meraplid;   // 申请单流
    private BigDecimal payamount; //支付金额
    private BigDecimal stlamount; //结算金额
    private BigDecimal fee; //手续费
    private BigDecimal fxrate; //汇率
    private String payccy; //支付币种
    private String stlccy; //结算币种
    private String applyid; //申请单号
    private String state; //状态
}
