package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包子卡 提现
@Data
public class WalletCardWithdrawRequest {
    private String cardno;       // 卡号
    private BigDecimal amount;   // 提现金额
}
