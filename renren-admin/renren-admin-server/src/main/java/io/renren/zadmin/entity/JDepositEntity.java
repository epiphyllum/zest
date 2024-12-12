package io.renren.zadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_deposit")
public class JDepositEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
    // 完成日期
    private Date statDate;

    // ID(6)
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private Integer api;

    // 卡充值信息
    private String meraplid;
    private String marketproduct;
    private String cardno;
    private String walletId;
    private String payerid;
    private BigDecimal amount;
    private String payeeaccount;
    private String procurecontent;
    private String agmfid;

    //
    private String currency;
    private BigDecimal txnAmount;        // 发起充值金额
    private BigDecimal merchantDeposit;  // 商户担保金
    private BigDecimal merchantCharge;   // 商户充值手续费
    private BigDecimal depositRate;      // 商户保证金比例
    private BigDecimal chargeRate;       // 商户充值手续费比例
    private BigDecimal costDepositRate;  // 成本-保证金比例
    private BigDecimal costChargeRate;   // 成本-充值手续费比例

    // 记录返回
    private BigDecimal securityamount;   // 通联保证金
    private BigDecimal fee;              // 通联手续费
    private String applyid;
    private String state;
    private String stateexplain;
    private String securitycurrency;
    private String feecurrency;
}