package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_withdraw
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_withdraw")
public class JWithdrawEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // (3+2)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // (6) ID相关
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private Integer api;

    // (8) 业务字段
    private String meraplid;
    private String cardno;
    private String payeeid;      // 提现到哪个账户
    private BigDecimal amount;
    private String currency;
    private String applyid;
    private String state;
    private String stateexplain;
    private BigDecimal securityamount;
    private Integer version;
}