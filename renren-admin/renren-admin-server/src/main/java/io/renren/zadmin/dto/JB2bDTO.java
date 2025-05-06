package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_b2b
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2025-04-23
*/
@Data
@Schema(description = "j_b2b")
public class JB2bDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理名称")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名")
    private String merchantName;

    @Schema(description = "state")
    private String state;
    @Schema(description = "error")
    private String error;

    @Schema(description = "通知ID")
    private String nid;
    @Schema(description = "业务关联ID")
    private String bid;

    @Schema(description = "通联内部虚拟号")
    private String acctno;
    @Schema(description = "币种")
    private String currency;

    @Schema(description = "交易代码")
    private String trxcod;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "入账时间")
    private String time;
    @Schema(description = "户名")
    private String payeraccountname;
    @Schema(description = "账号")
    private String payeraccountno;
    @Schema(description = "银行")
    private String payeraccountbank;
    @Schema(description = "国家")
    private String payeraccountcountry;
    @Schema(description = "附言")
    private String ps;

    @Schema(description = "附言")
    private String ecoMeraplid;
    @Schema(description = "附言")
    private String ecoApplyid;
    @Schema(description = "附言")
    private String funMeraplid;
    @Schema(description = "同名转账applyid")
    private String funApplyid;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}