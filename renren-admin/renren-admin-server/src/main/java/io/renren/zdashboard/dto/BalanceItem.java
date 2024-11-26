package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceItem {
    private BigDecimal cardSum;      // 商户卡充值
    private BigDecimal charge;       // 商户充值手续费
    private BigDecimal deposit;      // 商户担保金
    private BigDecimal cardFee;

    private BigDecimal aipCardSum;   // 通联发卡额
    private BigDecimal aipCharge;    // 通联充值手续费
    private BigDecimal aipDeposit;   // 通联担保金
    private BigDecimal aipCardFee;   //

    private String currency;
    private BigDecimal balance;      // va | subVa
}
