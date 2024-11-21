package io.renren.zapi.exchange.dto;

import lombok.Data;

@Data
public class ExchangeRateQuery {
    String currency;
    String settlecurrency;
}
