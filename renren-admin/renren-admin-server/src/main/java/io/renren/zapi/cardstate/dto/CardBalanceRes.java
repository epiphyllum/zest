package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardBalanceRes {
    private String cardno;       // 卡号
    private String currency;     // 币种
    private BigDecimal balance;  // 余额
}
