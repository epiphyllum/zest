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

    // 属于商户(2)
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Integer api;

    // 请求字段(5)
    private String meraplid;
    private String payeeccy;
    private String payerccy;
    private String lockamountflag;
    private BigDecimal amount;

    // 通联返回(2)
    private String applyid;
    private String state;

    // 锁汇情况(5)
    private String feecurrency;
    private BigDecimal settleamount;
    private String settlecurrency;
    private BigDecimal fxrate;
    private BigDecimal fee;

    // 执行情况(4)
    private String extype;
    private BigDecimal stlamount;
    private BigDecimal exfee;
    private BigDecimal exfxrate;
}