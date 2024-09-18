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

    // 子商户ID + 子商户名
    private Long subId;
    private String subName;

    // 商户ID
    private Long merchantId;
    private String merchantName;

    // 卡充值信息
    private String meraplid;
    private String cardno;
    private String payerid;
    private BigDecimal amount;
    private String currency;
    private String payeeaccount;
    private String procurecontent;
    private String agmfid;
    private String applyid;
    private Integer status;
}