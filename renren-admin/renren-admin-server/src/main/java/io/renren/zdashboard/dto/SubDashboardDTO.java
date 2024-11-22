package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubDashboardDTO {
    private String currency;                         //
    private StatItem balanceSummary;                 // 账户情况
    private StatItem todayStat;                          // 今日
    private StatItem monthStat;                      // 本月
    private List<PrepaidCardStat> prepaidStatList;  // 预防卡钱包
}
