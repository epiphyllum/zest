package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_money")
public class JMoneyEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 哪个商户
    private Long merchantId;
    private String merchantName;

    // 入账通知信息
    private String nid;
    private String bid;
    private String acctno;
    private BigDecimal amount;
    private String currency;
    private String trxcod;
    private String time;
    private String payeraccountname;
    private String payeraccountno;
    private String payeraccountbank;
    private String payeraccountcountry;
    private String ps;

    // 转而通知下游商户
    private Integer status;
    private Integer notifyStatus;
    private Integer notifyCount;
}