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
public class VWithdrawDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "提现额")
    private BigDecimal cardSum;
    @Schema(description = "平台手续费")
    private BigDecimal aipCharge;
    @Schema(description = "手续费")
    private BigDecimal charge;
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理名")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "产品")
    private String marketproduct;
    @Schema(description = "日期")
    private Date statDate;

}