package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 卡充值:  匿名卡， 实体卡， 实名卡
@Data
public class WalletCardChargeRequest {
    private String cardno;       // 卡号
    private BigDecimal amount;   // 金额
}
