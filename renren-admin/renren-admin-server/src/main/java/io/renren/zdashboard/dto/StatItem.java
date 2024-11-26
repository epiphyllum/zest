package io.renren.zdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StatItem {
    // 币种
    private String currency;                   // 币种
    // 充值统计
    private BigDecimal cardSum;                // 商户卡充值
    private BigDecimal charge;                 // 商户充值手续费
    private BigDecimal deposit;                // 商户担保金
    private BigDecimal aipCardSum;             // 通联发卡额
    private BigDecimal aipCharge;              // 通联充值手续费
    private BigDecimal aipDeposit;             // 通联担保金
    // 提现统计
    private BigDecimal withdraw;               // 提现额
    private BigDecimal withdrawCharge;         // 提现退商户手续费`
    private BigDecimal aipWithdrawCharge;      // 提现通联退手续费
    // 卡统计
    private Long totalCard;                    // 发卡总数
    private BigDecimal cardFee;                // 发卡收入
    private BigDecimal aipCardFee;             // 发卡成本
    // 交易数据
    private Long settlecount;                  // 交易笔数
    private BigDecimal settleamount;           // 交易金额
    // 日期
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private Date statDate;
}
