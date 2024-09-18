package io.renren.zapi.service.internal.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VaAccountRes {
    List<AccountItem> items;
}
