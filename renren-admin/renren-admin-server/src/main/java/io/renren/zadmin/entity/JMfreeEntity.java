package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_mfree
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_mfree")
public class JMfreeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 完成日期
    private Date statDate;

    //
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private BigDecimal amount;
    private String currency;
}