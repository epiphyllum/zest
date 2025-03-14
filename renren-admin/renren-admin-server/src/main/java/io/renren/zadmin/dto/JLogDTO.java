package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Data
@Schema(description = "j_log")
public class JLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    // 业务字段(3)
    @Schema(description = "归属方ID")
    private Long ownerId;
    @Schema(description = "归属方")
    private String ownerName;
    @Schema(description = "属主类型")
    private String ownerType;

    // 2
    @Schema(description = "余额类型")
    private String balanceType;
    @Schema(description = "余额ID")
    private Long balanceId;

    // 5
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "凭证类型")
    private Integer originType;
    @Schema(description = "记账类型")
    private Integer factType;
    @Schema(description = "凭证ID")
    private Long factId;
    @Schema(description = "凭证金额")
    private BigDecimal factAmount;
    @Schema(description = "凭证描述")
    private String factMemo;

    // 3
    @Schema(description = "旧余额")
    private BigDecimal oldBalance;
    @Schema(description = "新余额")
    private BigDecimal newBalance;
    @Schema(description = "新version")
    private Long version;
}