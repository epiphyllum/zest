package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@Schema(description = "j_auth")
public class JAuthDTO implements Serializable {
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

    // ID相关(6)
    @Schema(description = "代理ID")
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

    // 业务
    @Schema(description = "流水号")
    private String logkv;
    @Schema(description = "交易类型")
    private String trxcode;
    @Schema(description = "卡号")
    private String cardno;
    @Schema(description = "交易状态")
    private String state;
    @Schema(description = "交易状态说明")
    private String stateexplain;
    @Schema(description = "交易金额")
    private BigDecimal amount;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "交易时间")
    private String trxtime;
    @Schema(description = "交易方向")
    private String trxdir;
    @Schema(description = "交易地点")
    private String trxaddr;
    @Schema(description = "授权码")
    private String authcode;
    @Schema(description = "商户类别代码")
    private String mcc;
    @Schema(description = "通知时间")
    private String time;
}
