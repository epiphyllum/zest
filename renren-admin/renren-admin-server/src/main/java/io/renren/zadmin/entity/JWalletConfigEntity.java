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
    // 收费项
    private BigDecimal chargeRate;      // 卡充值1%
    private BigDecimal withdrawRate;    // 卡提现1%
    private BigDecimal usdtWithdrawFee; // 提U手续费
    private BigDecimal usdtSwapRate;    // 兑换U手续费
    private BigDecimal verifyFee;       // 实名认证手续费
    private BigDecimal vpaOpenFee;      // 开卡费
    private BigDecimal vccOpenFee;      // 开卡费
    private BigDecimal realOpenFee;     // 开卡费
    private BigDecimal vpaMonthFee;     // 月费
    private BigDecimal vccMonthFee;     // 月费
    private BigDecimal realMonthFee;    // 月费
    private BigDecimal upgradeFee;      // 账户升级费(USD)
    // 限制
    private BigDecimal minHkd;         // 卡片最小充值金额
    private BigDecimal minUsd;         // 卡片最小充值金额
    private BigDecimal minVaHkd;       // 商户va告警余额
    private BigDecimal minVaUsd;       // 商户va告警余额
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
    // tron节点基地址
    private String tronUrl;
    // 推广参数
    private BigDecimal s1OpenRate;
    private BigDecimal s2OpenRate;
    private BigDecimal s1ChargeRate;
    private BigDecimal s2ChargeRate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
