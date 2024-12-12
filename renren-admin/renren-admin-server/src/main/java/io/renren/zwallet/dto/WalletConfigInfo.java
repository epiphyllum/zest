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
    private BigDecimal minHkd;
    private BigDecimal minUsd;
    // 开卡费
    private BigDecimal vpaOpenFee;
    private BigDecimal vccOpenFee;
    private BigDecimal realOpenFee;
    private BigDecimal upgradeFee;  // 账户升级费
    // 电报配置
    private String telegramKey;
    private String telegramGroup;
    private String telegramHelp;
    // 港币汇率
    private BigDecimal hkdRate;
}