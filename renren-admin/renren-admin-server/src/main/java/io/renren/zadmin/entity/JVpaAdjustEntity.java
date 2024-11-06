package io.renren.zadmin.entity;

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

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private String maincardno;
    private String cardno;
    private BigDecimal adjustAmount;
    private BigDecimal oldQuote;
    private BigDecimal newQuote;
    private String state;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}