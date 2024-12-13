package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包充值 申请
@Data
public class WalletChargeRequest {
    private String currency;  // 充值币种
    private String network;   // 网络
}
