package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_mfree
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-21
*/
@Data
@Schema(description = "j_mfree")
public class JMfreeDTO implements Serializable {
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

    //
    @Schema(description = "完成日期")
    private Date statDate;

    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;

    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;

    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "币种")
    private String currency;

}