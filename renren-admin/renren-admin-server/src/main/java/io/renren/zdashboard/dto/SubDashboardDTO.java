package io.renren.zdashboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubDashboardDTO {
    private BalanceItem balanceSummary;              // 账户情况
    private StatItem todayStat;                      // 今日数据
    private List<StatItem> monthStat;                // 近30日数据
    private List<PrepaidCardStat> prepaidStatList;   // 预防卡钱包
}
