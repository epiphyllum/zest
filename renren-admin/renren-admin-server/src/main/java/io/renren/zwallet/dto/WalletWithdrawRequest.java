package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletWithdrawRequest {
    private BigDecimal amount; // 充值金额
    private String currency;   // 充值币种
}
