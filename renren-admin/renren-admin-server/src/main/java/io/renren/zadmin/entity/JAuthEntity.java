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

    // ID(6)
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private String marketproduct;

    private Long walletId;

    // 主卡
    private String maincardno;

    // 业务字段
    private String logkv;
    private String trxcode;
    private String cardno;
    private String state;
    private String stateexplain;

    private BigDecimal amount;
    private String currency;

    private BigDecimal settleamount;
    private String settlecurrency;

    private String trxtime;
    private String trxdir;
    private String trxaddr;
    private String authcode;
    private String mcc;
    private String time;
}