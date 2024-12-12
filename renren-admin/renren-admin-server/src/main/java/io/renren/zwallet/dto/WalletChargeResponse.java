package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包充值应答
@Data
public class WalletChargeResponse {
    private String payUrl;   // 收款地址
    private Long id;         // 充值ID
}
