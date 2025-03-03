package io.renren.zdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.List;

// 子商户Dashboard
@Data
public class DashboardSubDTO {
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private Date today;
    private String name;
    private BalanceItem balanceSummary;              // 资金账户
    private StatItem todayStat;                      // 今日概况
    private List<StatItem> monthStat;                // 业务统计
    private List<InMoneyItem> moneyStat;             // 最近30天入金数据
    private List<PrepaidCardStat> prepaidStatList;   // 预防卡钱包
}
