package io.renren.zadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_vpa_adjust
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_vpa_adjust")
public class JVpaAdjustEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 完成日期
    private Date statDate;

    //
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private String maincardno;
    private Long maincardid;
    private String currency;

    private Long walletId;
    private String walletName;

    private String marketproduct;
    private String cardno;
    private BigDecimal adjustAmount;
    private BigDecimal oldQuota;
    private BigDecimal newQuota;

    private String state;
    private String meraplid;
    private Integer api;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}