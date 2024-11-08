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
 * j_vpa_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_vpa_job")
public class JVpaJobEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private Integer api;

    private String sceneid;
    private String scenename;

    // 支持交易币种
    private String currency;
    private String cycle;
    private Integer authmaxcount;
    private BigDecimal authmaxamount;
    private String onlhkflag;

    private String begindate;
    private String enddate;

    private String naturalmonthflag;
    private String naturalmonthstartday;

    private String fixedamountflag;

    private String state;
    private BigDecimal merchantfee;
    private String feecurrency;

    private String maincardno;
    private String marketproduct;
    private Integer num;
    private String email;
    private String cardexpiredate;

    // 商户单号, 申请单号
    private String meraplid;
    private String applyid;
}