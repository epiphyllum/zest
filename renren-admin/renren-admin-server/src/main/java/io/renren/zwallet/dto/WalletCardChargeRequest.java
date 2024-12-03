package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 卡充值
@Data
public class WalletCardChargeRequest {
    private String cardno;
    private BigDecimal amount;
}
