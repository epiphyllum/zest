package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包开卡申请
@Data
public class WalletCardOpenRequest {
    private int num;
    private BigDecimal amount;
    private String currency;
}
