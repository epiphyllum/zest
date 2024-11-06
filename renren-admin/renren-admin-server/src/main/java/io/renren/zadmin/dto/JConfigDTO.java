package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-09
*/
@Data
@Schema(description = "j_config")
public class JConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "保证金扣率")
    private BigDecimal depositRate;
    @Schema(description = "手续费扣率")
    private BigDecimal chargeRate;

    @Schema(description = "L50")
    private BigDecimal l50;
    @Schema(description = "Gef50")
    private BigDecimal gef50;
    @Schema(description = "失败手续费")
    private BigDecimal failFee;
    @Schema(description = "争议处理费")
    private BigDecimal disputeFee;
    @Schema(description = "发卡限量")
    private Integer quotaLimit;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}