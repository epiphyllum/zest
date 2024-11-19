package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_stat
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-18
*/
@Data
@Schema(description = "j_stat")
public class JStatDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "md5")
    private String md5;
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "商户id")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户id")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "产品类型")
    private String marketproduct;
    @Schema(description = "统计日期")
    private Date statDate;
    @Schema(description = "充值总额")
    private BigDecimal cardSum;
    @Schema(description = "充值手续费")
    private BigDecimal charge;
    @Schema(description = "保证金")
    private BigDecimal deposit;
    @Schema(description = "充值总额-成本")
    private BigDecimal aipCardSum;
    @Schema(description = "充值手续费-成本")
    private BigDecimal aipCharge;
    @Schema(description = "保证金-成本")
    private BigDecimal aipDeposit;
    @Schema(description = "提现")
    private BigDecimal withdraw;
    @Schema(description = "提现手续费")
    private BigDecimal withdrawCharge;
    @Schema(description = "提现成本")
    private BigDecimal aipWithdrawCharge;
    @Schema(description = "发卡量")
    private Long totalCard;
    @Schema(description = "开卡费用")
    private BigDecimal cardFee;
    @Schema(description = "开卡成本")
    private BigDecimal aipCardFee;
}