package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * j_stat
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_stat")
public class JStatEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String md5;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private String currency;
    private String marketproduct;
    private Date statDate;

    private BigDecimal cardSum;
    private BigDecimal charge;
    private BigDecimal deposit;
    private BigDecimal aipCardSum;
    private BigDecimal aipCharge;
    private BigDecimal aipDeposit;
    private BigDecimal withdraw;
    private BigDecimal withdrawCharge;
    private BigDecimal aipWithdrawCharge;
    private Long totalCard;
    private BigDecimal cardFee;
    private BigDecimal aipCardFee;

}