package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

//
@Data
public class WalletWithdrawRequest {
    private BigDecimal amount;    // 提现金额
    private String currency;      // 充值币种: USD/HKD
    private BigDecimal payAmount; // 实际转账金额
    private String usdtAddress;   // 提现地址
}