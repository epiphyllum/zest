package io.renren.zin.exchange.dto;

import lombok.Data;

@Data
public class TExchangeRateRequest {
    String currency;
    String settlecurrency;
}
