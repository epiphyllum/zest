package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_deposit")
public class JDepositEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // ID(6)
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private Integer api;

    // 卡充值信息
    private String meraplid;
    private String cardno;
    private String payerid;
    private BigDecimal amount;
    private String payeeaccount;
    private String procurecontent;
    private String agmfid;

    // 冗余添加的
    private String currency;
    private BigDecimal txnAmount;

    // 记录返回
    private String applyid;
    private String state;
    private String stateexplain;
    private BigDecimal securityamount;
    private BigDecimal fee;

}