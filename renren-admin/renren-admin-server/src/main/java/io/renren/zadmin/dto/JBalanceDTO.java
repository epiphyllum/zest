package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_balance
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-17
*/
@Data
@Schema(description = "j_balance")
public class JBalanceDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 5
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

    // 业务字段(7)
    @Schema(description = "属主ID")
    private Long ownerId;
    @Schema(description = "属主名称")
    private String ownerName;
    @Schema(description = "属主类型")
    private String ownerType;
    @Schema(description = "余额类型")
    private String balanceType;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "余额")
    private BigDecimal balance;
    @Schema(description = "冻结额")
    private BigDecimal frozen;
    @Schema(description = "版本号")
    private Integer version;
}