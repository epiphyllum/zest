package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* j_channel_log
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-15
*/
@Data
@Schema(description = "j_channel_log")
public class JChannelLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    @Schema(description = "请求ID")
    private String reqId;
    @Schema(description = "接口名称")
    private String apiName;
    @Schema(description = "收到")
    private String recv;
    @Schema(description = "发送")
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