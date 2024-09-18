package io.renren.zapi.service.internal.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InMoneyAccountRes {
    List<AccountItem> items;
}
