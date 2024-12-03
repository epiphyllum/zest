package io.renren.zdashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceItem {
    private Long totalCard;          // 发卡总数

    private BigDecimal cardSum;      // 发卡额
    private BigDecimal charge;       // 充值手续费
    private BigDecimal deposit;      // 担保金
    private BigDecimal cardFee;      // 开卡费

    private BigDecimal aipCardSum;   // 通联发卡额
    private BigDecimal aipCharge;    // 通联充值手续费
    private BigDecimal aipDeposit;   // 通联担保金
    private BigDecimal aipCardFee;   // 通联开卡成本

    private BigDecimal settleamount; // 清算总金额
    private Long settlecount;        // 清算总笔数

    private String currency;         // 币种
    private BigDecimal balance;      // 账户余额

    public static BalanceItem zero(String currency) {
        BalanceItem item = new BalanceItem();
        item.setBalance(BigDecimal.ZERO);
        item.setCurrency(currency);

        item.setAipCharge(BigDecimal.ZERO);
        item.setAipCardSum(BigDecimal.ZERO);
        item.setAipDeposit(BigDecimal.ZERO);
        item.setAipCardFee(BigDecimal.ZERO);

        item.setCharge(BigDecimal.ZERO);
        item.setCardSum(BigDecimal.ZERO);
        item.setDeposit(BigDecimal.ZERO);
        item.setCardFee(BigDecimal.ZERO);
        return item;
    }
}
