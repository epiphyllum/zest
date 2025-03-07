package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_log")
public class JLogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 属主(3)
    private Long ownerId;
    private String ownerName;
    private String ownerType;  // merchant, sub

    // 对哪个账户的记账(4)
    private String balanceType;
    private Long balanceId;
    private String currency;

    // 记账详细信息(8)
    private Integer originType;
    private Integer factType;
    private Long factId;
    private BigDecimal factAmount;
    private String factMemo;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;
    private Long version;
}