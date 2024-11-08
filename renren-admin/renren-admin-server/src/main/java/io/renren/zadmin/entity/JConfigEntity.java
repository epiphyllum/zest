package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_config")
public class JConfigEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    private BigDecimal depositRate; // 保证金扣率
    private BigDecimal chargeRate;  // 充值手续费扣率
    private BigDecimal failFee;     // 失败费
    private BigDecimal l50;         //
    private BigDecimal gef50;       //
    private BigDecimal disputeFee;  //
    private Integer quotaLimit;     //

    private String vccMainReal ;    //
    private String vccMainVirtual ; //
}