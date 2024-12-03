package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletCardOpenRequest {
    private int num;
    private BigDecimal amount;
}
