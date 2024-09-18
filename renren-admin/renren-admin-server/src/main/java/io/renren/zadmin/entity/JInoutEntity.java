package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * 资金调拨
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_inout")
public class JInoutEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 商户资金调拨
    private Long merchantId;
    private String merchantName;

    // 调拨类型:  i2v,  v2i, s2m, m2s
    private String type;
    private BigDecimal amount;
    private String currency;

    // 商户ID/子商户ID,
    private Long fromId;
    private Long toId;
    private Long fromName;
    private Long toName;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}