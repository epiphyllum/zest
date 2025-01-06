package io.renren.zwallet.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.commons.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
public class WalletConfigInfo {
    // 充值费比例
    private BigDecimal chargeRate;
    // 最小充值金额
    private BigDecimal minHkd;        // 港币最小充值金额
    private BigDecimal minUsd;        // 美元最小充值金额
    // 开卡费
    private BigDecimal vpaOpenFee;    // 虚拟卡开卡费
    private BigDecimal vccOpenFee;    // 实名卡开卡费
    private BigDecimal realOpenFee;   // 实体卡开卡费
    // 月费
    private BigDecimal vpaMonthFee;   // 虚拟卡月费
    private BigDecimal vccMonthFee;   // 实名卡月费
    private BigDecimal realMonthFee;  // 实体卡月费
    // 账户升级费(USD)
    private BigDecimal upgradeFee;    // 账户升级费(USD)
    // 港币/美元 汇率
    private BigDecimal hkdRate;       // usd/hkd 汇率
    // 电报配置
    private String telegramKey;       // 电报机器人密钥
    private String telegramGroup;     //
    private String telegramHelp;      //
}