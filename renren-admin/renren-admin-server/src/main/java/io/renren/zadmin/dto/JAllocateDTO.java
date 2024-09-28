package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_inout
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Data
@Schema(description = "j_allocate")
public class JAllocateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 通用(5)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

    // ID(6)
    @Schema(description = "代理id")
    private Long agentId;
    @Schema(description = "代理名称")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名称")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户名称")
    private String subName;

    // 业务字段
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "交易类型")
    private String type;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "出金账户")
    private Long fromId;
    @Schema(description = "入金账户")
    private Long toId;
    @Schema(description = "出金账户")
    private Long fromName;
    @Schema(description = "入金账户")
    private Long toName;
}