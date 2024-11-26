package io.renren.zdashboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentDashboardDTO {
    private BalanceItem balanceSummary;                 // 账户情况
    private StatItem todayStat;                      // 今日
    private List<StatItem> monthStat;                      // 本月
    private List<PrepaidCardStat> prepaidStatList;   // 预防卡钱包
}
