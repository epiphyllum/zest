package io.renren.zdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

// 机构Dashboard
@Data
public class DashboardOperationDTO {
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private Date today;                              // 今日
    private String name;                             // 姓名
    private BigDecimal va;                           // 账户
    private BalanceItem balanceSummary;              // 账户情况
    private StatItem todayStat;                      // 今日
    private List<StatItem> monthStat;                // 本月
    private List<InMoneyItem> moneyStat;             // 最近30天入金数据
    private List<PrepaidCardStat> prepaidCardStats;  // 预防卡钱包
}
