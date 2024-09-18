package io.renren.zapi.service.internal.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountItem {
    String currency;
    BigDecimal balance;
}
