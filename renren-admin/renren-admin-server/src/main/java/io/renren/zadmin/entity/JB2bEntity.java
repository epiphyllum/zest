package io.renren.zadmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.commons.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * j_b2b
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2025-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_b2b")
public class JB2bEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;

    private String state;
    private String nid;
    private String bid;
    private String acctno;
    private String currency;

    private String trxcod;
    private BigDecimal amount;
    private String time;
    private String payeraccountname;
    private String payeraccountno;
    private String payeraccountbank;
    private String payeraccountcountry;
    private String ps;

    private String ecoMeraplid;
    private String ecoApplyid;
    private String funMeraplid;
    private String funApplyid;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}