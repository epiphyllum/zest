package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_pay_channel
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@Data
@Schema(description = "j_pay_channel")
public class JPayChannelDTO implements Serializable {
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

    @Schema(description = "扣率")
    private BigDecimal chargeRate;
    @Schema(description = "保底")
    private BigDecimal floor;
    @Schema(description = "封顶")
    private BigDecimal ceiling;

    @Schema(description = "渠道代码")
    private String channelCode;
    @Schema(description = "渠道名称")
    private String channelName;
    @Schema(description = "结算币种")
    private String stlCurrency;
    @Schema(description = "商户号")
    private String merchantNo;

    @Schema(description = "接口地址")
    private String apiUrl;
    @Schema(description = "我方公钥")
    private String publicKey;
    @Schema(description = "我方私钥")
    private String privateKey;
    @Schema(description = "渠道公钥")
    private String channelKey;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}