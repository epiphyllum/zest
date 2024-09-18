package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@Data
@Schema(description = "j_money")
public class JMoneyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "大吉")
    private Long deptId;
    @Schema(description = "通知id")
    private String nid;
    @Schema(description = "业务关联id")
    private String bid;
    @Schema(description = "账号")
    private String acctno;
    @Schema(description = "变动金额")
    private BigDecimal amount;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "交易类型")
    private String trxcod;
    @Schema(description = "入账时间")
    private String time;
    @Schema(description = "打款方姓名")
    private String payeraccountname;
    @Schema(description = "打款方银行账号")
    private String payeraccountno;
    @Schema(description = "打款方银行号")
    private String payeraccountbank;
    @Schema(description = "打款方国家")
    private String payeraccountcountry;
    @Schema(description = "附言")
    private String ps;
    @Schema(description = "商户号")
    private Long merchantId;
    @Schema(description = "商户名")
    private String merchantName;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "通知状态")
    private Integer notifyStatus;
    @Schema(description = "通知次数")
    private Integer notifyCount;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;
}