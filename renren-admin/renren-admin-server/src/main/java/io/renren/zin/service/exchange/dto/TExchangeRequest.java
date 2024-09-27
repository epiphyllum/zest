package io.renren.zin.service.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TExchangeRequest {
    private String meraplid;       // 申请单流水
    private String payeemerid;     // 到账商户
    private String payeeccy;       // 到账币种
    private String payerccy;       // 卖出币种
    private BigDecimal amount;     // 金额: 与lockamountflag有关系
    private String lockamountflag; // 锁定方
}
