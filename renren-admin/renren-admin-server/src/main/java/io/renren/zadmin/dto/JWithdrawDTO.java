package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_withdraw
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@Schema(description = "j_withdraw")
public class JWithdrawDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

    // ID相关
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "是否API")
    private Integer api;

    // 业务字段
    @Schema(description = "申请单流水")
    private String meraplid;
    @Schema(description = "产品")
    private String marketproduct;
    @Schema(description = "卡号")
    private String cardno;
    @Schema(description = "交易对手")
    private String payeeid;
    @Schema(description = "缴纳金额")
    private BigDecimal amount;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "申请单号")
    private String applyid;
    @Schema(description = "state")
    private String state;
    @Schema(description = "状态解释")
    private String stateexplain;

    @Schema(description = "担保金额")
    private String securityamount;
    @Schema(description = "担保金币种")
    private String securitycurrency;
    @Schema(description = "通联手续费")
    private BigDecimal fee;
    @Schema(description = "费用币种")
    private String feecurrency;
    // 退商户手续费
    @Schema(description = "退商户手续费")
    private BigDecimal merchantfee;

    // 提取otp
    private String otp;
}