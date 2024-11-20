package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrepaidCardStat {
    private String cardno;            // 主卡卡号
    private BigDecimal prepaidQuota;  // 发卡额度
    private BigDecimal prepaidSum;    // 发卡总额
    private Long totalCard;        // 发卡数量
    private BigDecimal balance;       // 可用余额
}
