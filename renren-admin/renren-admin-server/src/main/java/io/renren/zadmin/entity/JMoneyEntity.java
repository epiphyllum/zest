package io.renren.zadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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

    // 完成日期
    private Date statDate;

    // (4) ID
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;

    //
    private Integer api;
    private String  meraplid;

    //
    private String referencecode;
    private String applyid;
    private String cardno;
    private String cardname;

    // 确认时用
    private BigDecimal applyAmount;
    private String transferfid;
    private String otherfid;

    // 入账通知信息(12)
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

    // 状态
    private String state;
    private String stateexplain;

    // 转而通知下游商户(3)
    private Integer notifyStatus;
    private Integer notifyCount;
}