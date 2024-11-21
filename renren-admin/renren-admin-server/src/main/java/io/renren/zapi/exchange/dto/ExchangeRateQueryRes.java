package io.renren.zapi.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateQueryRes {
    String currency;
    String settlecurrency;
    BigDecimal fxrate;
}
