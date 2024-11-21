package io.renren.zin.exchange.dto;

import io.renren.zin.TResult;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TExchangeRateResponse extends TResult {
    String currency;
    String settlecurrency;
    BigDecimal fxrate;
}
