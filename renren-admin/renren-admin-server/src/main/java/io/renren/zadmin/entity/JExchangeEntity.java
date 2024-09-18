package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_exchange
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_exchange")
public class JExchangeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 属于商户
    private Long merchantId;
    private String merchantName;

    private String meraplid;
    private String payeeccy;
    private String payerccy;
    private BigDecimal amount;
    private String applyid;

    // 锁汇情况
    private String feecurrency;
    private BigDecimal settleamount;
    private String settlecurrency;
    private BigDecimal fxrate;
    private BigDecimal fee;
    private String extype;

    // 执行情况
    private BigDecimal stlamount;
    private BigDecimal exfee;
    private BigDecimal exfxrate;

    // 状态
    private String state;
}