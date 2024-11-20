package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatItem {
    private String currency;    // 币种
    private BigDecimal cardSum; // 发卡总额
    private BigDecimal cardFee; // 发卡手续费
    private BigDecimal charge;  // 充值手续费
    private BigDecimal deposit; // 保证金
}
