package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 数字货币提现应答
@Data
public class CoinWithdrawResponse {
    private BigDecimal amount; // 提现金额
    private String currency;   // 提现币种: USD, HKD
}
