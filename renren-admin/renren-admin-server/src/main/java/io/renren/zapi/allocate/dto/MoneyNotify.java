package io.renren.zapi.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

// 入金通知:  我们只需要告诉客户多少钱入账
@Data
public class MoneyNotify {
    String currency;
    BigDecimal amount;     //

    String payeraccountno; //
}

