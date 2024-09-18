package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_auth")
public class JAuthEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 需要分拣到子商户
    @TableField(fill = FieldFill.INSERT)
    private Long deptId;
    private String deptName;

    // 冗余便于查找
    private Long merchantId;
    private String merchantName;

    private String cardno;
    private String logkv;
    private String trxtype;
    private String trxdir;
    private String state;
    private BigDecimal amount;
    private String currency;
    private String trxtime;
    private String mcc;
    private String trxaddr;
    private String authcode;


}