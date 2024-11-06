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

    /**
     * 保证金扣率
     */
    private BigDecimal depositRate;
    /**
     * 手续费扣率
     */
    private BigDecimal chargeRate;

    private BigDecimal failFee;
    private BigDecimal l50;
    private BigDecimal gef50;
    private BigDecimal disputeFee;
    private Integer quotaLimit;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}