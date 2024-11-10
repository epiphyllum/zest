package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_vpa_adjust
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-02
*/
@Data
@Schema(description = "j_vpa_adjust")
public class JVpaAdjustDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "代理id")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;
    @Schema(description = "卡产品")
    private String marketproduct;
    @Schema(description = "vpa子卡")
    private String cardno;
    @Schema(description = "vpa主卡")
    private String maincardno;
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;
    @Schema(description = "调整期额度")
    private BigDecimal oldQuota;
    @Schema(description = "调整后额度")
    private BigDecimal newQuota;

    @Schema(description = "状态")
    private String state;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}