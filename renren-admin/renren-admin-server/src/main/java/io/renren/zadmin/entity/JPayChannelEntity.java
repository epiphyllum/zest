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
 * j_pay_channel
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_pay_channel")
public class JPayChannelEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;

    private BigDecimal chargeRate;
    private BigDecimal floor;
    private BigDecimal ceiling;

    private Integer enabled;    // 启用
    private Integer weight;     // 权重

    private String channelCode; // 渠道代码
    private String channelName; // 渠道名称
    private String stlCurrency; // 结算币种
    private String payCurrency; // 结算币种
    private String merchantNo;  // 接入商户号

    private String apiUrl;
    private String publicKey;
    private String privateKey;
    private String channelKey;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}