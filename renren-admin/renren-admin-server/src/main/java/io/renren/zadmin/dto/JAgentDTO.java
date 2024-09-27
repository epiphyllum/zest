package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_agent
*
* @author Mark sunlightcs@gmail.com
* @since 3.0 2024-08-16
*/
@Data
@Schema(description = "j_agent")
public class JAgentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 通用
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

    // 代理名
    @Schema(description = "代理名称")
    private String agentName;

    // 业务字段
    @Schema(description = "充值费率")
    private BigDecimal chargeRate;
    @Schema(description = "账号")
    private String accountNo;
    @Schema(description = "账户名")
    private String accountUser;
    @Schema(description = "银行名")
    private String accountBank;
    @Schema(description = "一档金额")
    private BigDecimal firstLimit;
    @Schema(description = "一档费率")
    private BigDecimal firstRate;
    @Schema(description = "二挡金额")
    private BigDecimal secondLimit;
    @Schema(description = "二挡费率")
    private BigDecimal secondRate;
    @Schema(description = "三挡金额")
    private BigDecimal thirdLimit;
    @Schema(description = "三挡费率")
    private BigDecimal thirdRate;
}