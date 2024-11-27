package io.renren.zdashboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubDashboardDTO {
    private BalanceItem balanceSummary;              // 资金账户
    private StatItem todayStat;                      // 今日概况
    private List<StatItem> monthStat;                // 业务统计
    private List<InMoneyItem> moneyStat;             // 最近30天入金数据
    private List<PrepaidCardStat> prepaidStatList;   // 预防卡钱包
}
