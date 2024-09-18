package io.renren.zapi.service.money.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyInNotify {
    String currency;
    BigDecimal amount;
}

