package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包充值 申请
@Data
public class WalletChargeRequest {
    private BigDecimal amount;      // 充值金额
    private String currency;        // 充值币种
    private String payCurrency;     // 付款币种, 目前只支持USDT
    private BigDecimal payAmount;   // 付款金额
}
