package io.renren.zapi.service.card.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardBalanceRes {
    private String cardno;       // 卡号
    private String currency;     // 币种
    private BigDecimal balance;  // 余额
}
