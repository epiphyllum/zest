package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_agent
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.0 2024-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_agent")
public class JAgentEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // 3+2
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 5
    private String agentName;
    private BigDecimal chargeRate;
    private String accountNo;
    private String accountUser;
    private String accountBank;

    // 6
    private BigDecimal firstLimit;
    private BigDecimal firstRate;
    private BigDecimal secondLimit;
    private BigDecimal secondRate;
    private BigDecimal thirdLimit;
    private BigDecimal thirdRate;
}