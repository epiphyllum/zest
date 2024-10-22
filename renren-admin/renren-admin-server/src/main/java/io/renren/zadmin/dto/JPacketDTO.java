package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* j_packet
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-13
*/
@Data
@Schema(description = "j_packet")
public class JPacketDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理名")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名")
    private String merchantName;

    @Schema(description = "IP")
    private String ip;
    @Schema(description = "请求ID")
    private String reqId;
    @Schema(description = "接口名称")
    private String apiName;
    @Schema(description = "报文内容")
    private String recv;
    @Schema(description = "报文内容")
    private String send;
    @Schema(description = "签名")
    private String sign;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}