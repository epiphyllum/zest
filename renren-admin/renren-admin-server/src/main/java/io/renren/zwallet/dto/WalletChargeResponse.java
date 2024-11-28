package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletChargeResponse {
    private String payUrl;
    private Long id;
}
