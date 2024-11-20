package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubDashboardDTO {
    private StatItem balanceSummary; // 账户情况
    private StatItem today; // 今日
    private StatItem monthStat; // 本月
    private List<PrepaidCardStat> prepaidCardStats;  // 预防卡钱包
}
