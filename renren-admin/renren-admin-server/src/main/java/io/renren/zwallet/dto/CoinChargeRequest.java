package io.renren.zwallet.dto;

import lombok.Data;

// 钱包充值 申请
@Data
public class CoinChargeRequest {
    private String currency;  // 充值币种
    private String network;   // 网络
}
