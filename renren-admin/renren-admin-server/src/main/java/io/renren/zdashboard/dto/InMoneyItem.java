package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InMoneyItem {
    private Date statDate;
    private BigDecimal amount;
    private Long count;
}
