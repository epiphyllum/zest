package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@Schema(description = "j_wallet_config")
public class JWalletConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "代理id")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;

    ////////////////////////////////////////////
    @Schema(description = "用户充值手续费率")
    private BigDecimal chargeRate;
    @Schema(description = "用户提现手续费率")
    private BigDecimal withdrawRate;

    @Schema(description = "最小充值港币")
    private BigDecimal minHkd;
    @Schema(description = "最小充值美金")
    private BigDecimal minUsd;

    @Schema(description = "最小va港币")
    private BigDecimal minVaHkd;
    @Schema(description = "最小va美金")
    private BigDecimal minVaUsd;

    ////////////////////////////////////////////
    @Schema(description = "匿名卡开卡费")
    private BigDecimal vpaOpenFee;
    @Schema(description = "实名卡开卡费")
    private BigDecimal vccOpenFee;
    @Schema(description = "实体卡开卡费")
    private BigDecimal realOpenFee;

    ////////////////////////////////////////////
    @Schema(description = "匿名卡月费")
    private BigDecimal vpaMonthFee;
    @Schema(description = "实名卡月费")
    private BigDecimal vccMonthFee;
    @Schema(description = "实体月费")
    private BigDecimal realMonthFee;

    @Schema(description = "升级费用")
    private BigDecimal upgradeFee;

    ////////////////////////////////////////////
    @Schema(description = "邮箱配置")
    private String mailHost;
    @Schema(description = "邮箱配置")
    private String mailPort;
    @Schema(description = "邮箱配置")
    private String mailUser;
    @Schema(description = "邮箱配置")
    private String mailPass;
    @Schema(description = "邮箱配置")
    private String mailFrom;

    @Schema(description = "域名")
    private String domain;
    @Schema(description = "协议")
    private String protocol;
    @Schema(description = "港币汇率")
    private BigDecimal hkdRate;
    @Schema(description = "telegramKey")
    private String telegramKey;
    @Schema(description = "telegramGroup")
    private String telegramGroup;
    @Schema(description = "telegramHelp")
    private String telegramHelp;

    ////////////////////////////////////////////
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}