package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@Schema(description = "j_deposit")
public class JDepositDTO implements Serializable {
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

    // 商户ID
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "商户ID")
    private Long subId;
    @Schema(description = "子商户名称")
    private String subName;

    // fact
    @Schema(description = "申请单")
    private String meraplid;
    @Schema(description = "卡号")
    private String cardno;
    @Schema(description = "付款id")
    private String payerid;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "交易对上")
    private String payeeaccount;
    @Schema(description = "采购内容")
    private String procurecontent;
    @Schema(description = "合同文件")
    private String agmfid;
    @Schema(description = "申请单号")
    private String applyid;
    @Schema(description = "状态")
    private Integer status;
}