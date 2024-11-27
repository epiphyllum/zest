package io.renren.zdashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import feign.FeignException;
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
    // 入金: 商户|代理|机构
    private BigDecimal inMoney;                // 入金金额
    private Long inMoneyCount;                 // 入金笔数
    private BigDecimal outMoney;               // 出金金额
    private Long outMoneyCount;                // 出金笔数
    // 日期
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private Date statDate;

    public static StatItem zero(String currency, Date statDate) {
        StatItem item = new StatItem();
        item.setSettlecount(0L);
        item.setSettleamount(BigDecimal.ZERO);
        item.setInMoney(BigDecimal.ZERO);
        item.setInMoneyCount(0L);
        item.setOutMoney(BigDecimal.ZERO);
        item.setOutMoneyCount(0L);
        item.setAipCharge(BigDecimal.ZERO);
        item.setCharge(BigDecimal.ZERO);
        item.setWithdraw(BigDecimal.ZERO);
        item.setWithdrawCharge(BigDecimal.ZERO);
        item.setDeposit(BigDecimal.ZERO);
        item.setAipDeposit(BigDecimal.ZERO);
        item.setTotalCard(0L);
        item.setCardFee(BigDecimal.ZERO);
        item.setAipCardFee(BigDecimal.ZERO);
        item.setCardSum(BigDecimal.ZERO);
        item.setAipCardSum(BigDecimal.ZERO);
        item.setStatDate(statDate);
        item.setCurrency(currency);
        item.setAipWithdrawCharge(BigDecimal.ZERO);
        return item;
    }
}
