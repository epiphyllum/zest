package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * VIEW
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("v_deposit")
public class VDepositEntity {
    private static final long serialVersionUID = 1L;

    private BigDecimal charge;
    private BigDecimal deposit;
    private BigDecimal aipCharge;
    private BigDecimal aipDeposit;
    private BigDecimal cardSum;
    private BigDecimal aipCardSum;
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private String currency;
    private String marketproduct;
    private Date statDate;
}