package io.renren.zdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.List;

// 代理Dashboard
@Data
public class DashboardAgentDTO {
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private Date today;                              // 今日日期
    private String name;                             // 名称
    private BalanceItem balanceSummary;              // 资金账户: 当前账户情况
    private StatItem todayStat;                      // 今日概况: 当天业务情况
    private List<StatItem> monthStat;                // 业务统计: 最近30天各项数据的情况
    private List<InMoneyItem> moneyStat;             // 入金统计: 最近30天的入金数据情况
    private List<PrepaidCardStat> prepaidStatList;   // 钱包列表:
}
