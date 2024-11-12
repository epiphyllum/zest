package io.renren.zadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_fee_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_fee_config")
public class JFeeConfigEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private String marketproduct;

    private BigDecimal costCardFee;
    private BigDecimal costL50;
    private BigDecimal costGef50;
    private BigDecimal costFailFee;
    private BigDecimal costDisputeFee;

    private BigDecimal cardFee;
    private BigDecimal l50;
    private BigDecimal gef50;
    private BigDecimal failFee;
    private BigDecimal disputeFee;
}