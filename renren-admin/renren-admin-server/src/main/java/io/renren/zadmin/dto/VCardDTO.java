package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* VIEW
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-18
*/
@Data
@Schema(description = "VIEW")
public class VCardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "发卡量")
    private Long totalCard;
    @Schema(description = "发卡成本")
    private BigDecimal fee;
    @Schema(description = "发卡手续费")
    private BigDecimal merchantfee;
    @Schema(description = "日期")
    private Date statDate;
    @Schema(description = "卡的币种")
    private String currency;
    @Schema(description = "对外卡产品")
    private String marketproduct;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户id")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "代理id")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;

}