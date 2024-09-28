package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_va
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_va")
public class JVaEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // (3+2) 通用
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // (4) 业务字段
    private String accountno;
    private String currency;
    private BigDecimal amount;
    private String vaaccountno;
}