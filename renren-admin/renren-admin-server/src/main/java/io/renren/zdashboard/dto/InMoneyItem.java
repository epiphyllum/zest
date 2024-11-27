package io.renren.zdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InMoneyItem {
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private Date statDate;
    private BigDecimal amount;
    private Long count;
}
