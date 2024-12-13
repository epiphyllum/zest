package io.renren.zadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_wallet_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_wallet_config")
public class JWalletConfigEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // ID tag
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    // 充值/提现:费例
    private BigDecimal chargeRate;
    private BigDecimal withdrawRate;
    // 最小充值金额
    private BigDecimal minHkd;
    private BigDecimal minUsd;
    // va最小余额
    private BigDecimal minVaHkd;
    private BigDecimal minVaUsd;
    // 开卡费
    private BigDecimal vpaOpenFee;
    private BigDecimal vccOpenFee;
    private BigDecimal realOpenFee;
    // 月费
    private BigDecimal vpaMonthFee;
    private BigDecimal vccMonthFee;
    private BigDecimal realMonthFee;
    // 账户升级费
    private BigDecimal upgradeFee;
    // 电报配置
    private String telegramKey;
    private String telegramGroup;
    private String telegramHelp;
    // 对外接口
    private String domain;
    private String protocol;
    // 港币汇率
    private BigDecimal hkdRate;
    // 邮箱配置与域名
    private String mailHost;
    private String mailPort;
    private String mailUser;
    private String mailPass;
    private String mailFrom;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}