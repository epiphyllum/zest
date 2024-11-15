package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_fee_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-12
*/
@Data
@Schema(description = "j_fee_config")
public class JFeeConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理名称")
    private String agentName;

    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "产品")
    private String marketproduct;
    @Schema(description = "本币币种")
    private String currency;

    @Schema(description = "开卡收费")
    private BigDecimal costCardFee;

    @Schema(description = "保证金扣率-成本")
    private BigDecimal costDepositRate;
    @Schema(description = "手续费扣率-成本")
    private BigDecimal costChargeRate;

    @Schema(description = "小金额手续费每笔")
    private BigDecimal costL50;
    @Schema(description = "失败手续费")
    private BigDecimal costGef50;
    @Schema(description = "失败费/笔, 当失败率&gt;15%")
    private BigDecimal costFailFee;
    @Schema(description = "争议处理费")
    private BigDecimal costDisputeFee;

    @Schema(description = "保证金扣率")
    private BigDecimal depositRate;
    @Schema(description = "手续费扣率")
    private BigDecimal chargeRate;
    @Schema(description = "小金额手续费每笔")
    private BigDecimal l50;
    @Schema(description = "&gt;=50 fail 手续费")
    private BigDecimal gef50;
    @Schema(description = "失败费/笔, faiL_rate &gt; 15%")
    private BigDecimal failFee;
    @Schema(description = "争议处理费")
    private BigDecimal disputeFee;
    @Schema(description = "开卡收费")
    private BigDecimal cardFee;

    ///////////////////////////////////
    @Schema(description = "va余额")
    private BigDecimal subVa;
    @Schema(description = "最大到账金额")
    private BigDecimal maxAmount;

}