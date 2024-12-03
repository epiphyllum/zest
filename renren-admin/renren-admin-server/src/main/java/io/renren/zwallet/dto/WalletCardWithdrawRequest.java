package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletCardWithdrawRequest {
    private String cardno;
    private BigDecimal amount;
}
