package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * VIEW
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("v_card")
public class VCardEntity {
    private static final long serialVersionUID = 1L;

    private Long totalCard;
    private BigDecimal fee;
    private BigDecimal merchantfee;
    private Date statDate;
    private String currency;
    private String marketproduct;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private Long agentId;
    private String agentName;
}