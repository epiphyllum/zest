package io.renren.zapi.service.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

// 入金通知
@Data
public class MoneyNotify {
    String currency;
    BigDecimal amount;
}

