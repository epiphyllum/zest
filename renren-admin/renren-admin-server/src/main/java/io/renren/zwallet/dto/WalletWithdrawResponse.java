package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包提现
@Data
public class WalletWithdrawResponse {
    private BigDecimal amount; // 提现金额
    private String currency;   // 提现币种: USD, HKD
}
