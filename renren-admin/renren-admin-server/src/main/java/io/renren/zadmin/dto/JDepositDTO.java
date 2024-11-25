package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@Schema(description = "j_deposit")
public class JDepositDTO implements Serializable {
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

    //
    @Schema(description = "完成日期")
    private Date statDate;

    // 商户ID(6)
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "商户ID")
    private Long subId;
    @Schema(description = "子商户名称")
    private String subName;
    private Integer api;

    // fact
    @Schema(description = "申请单")
    private String meraplid;
    @Schema(description = "卡产品")
    private String marketproduct;
    @Schema(description = "卡号")
    private String cardno;
    @Schema(description = "付款id")
    private String payerid;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "交易对上")
    private String payeeaccount;
    @Schema(description = "采购内容")
    private String procurecontent;
    @Schema(description = "合同文件")
    private String agmfid;

    // 冗余增加的
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "发起金额")
    private BigDecimal txnAmount;

    @Schema(description = "商户保证")
    private BigDecimal merchantDeposit;
    @Schema(description = "商户手续费")
    private BigDecimal merchantCharge;
    @Schema(description = "商户保证金比例")
    private BigDecimal depositRate;
    @Schema(description = "商户手续费比例")
    private BigDecimal chargeRate;
    @Schema(description = "保证金比例-成本")
    private BigDecimal costDepositRate;
    @Schema(description = "手续费比例-成本")
    private BigDecimal costChargeRate;

    // 通联返回
    @Schema(description = "申请单号")
    private String applyid;
    @Schema(description = "状态")
    private String state;

    // 查询或者通知
    @Schema(description = "解释")
    private String stateexplain;
    @Schema(description = "担保金")
    private BigDecimal securityamount;
    @Schema(description = "担保金币种")
    private String securitycurrency;
    @Schema(description = "通联手续费")
    private BigDecimal fee;
    @Schema(description = "费用币种")
    private String feecurrency;

    //
    private String otp;

}
