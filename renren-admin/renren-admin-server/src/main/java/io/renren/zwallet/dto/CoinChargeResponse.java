package io.renren.zwallet.dto;

import lombok.Data;

// 钱包充值应答
@Data
public class CoinChargeResponse {
    private String address;   // 收款地址
}
