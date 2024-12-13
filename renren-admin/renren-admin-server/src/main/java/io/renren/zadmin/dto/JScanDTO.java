package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_tron
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-12-13
*/
@Data
@Schema(description = "j_tron")
public class JScanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理名称")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名称")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户名称")
    private String subName;
    @Schema(description = "钱包ID")
    private Long walletId;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "网络")
    private String network;
    @Schema(description = "转出地址")
    private String fromAddress;
    @Schema(description = "转入地址")
    private String toAddress;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "时间")
    private Long ts;
    @Schema(description = "方向")
    private String flag;
    @Schema(description = "交易哈希")
    private String txid;
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