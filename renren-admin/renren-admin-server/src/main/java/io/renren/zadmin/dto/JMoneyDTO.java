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

    // 通用(5)
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

    // id相关(4)
    @Schema(description = "代理id")
    private Long agentId;
    @Schema(description = "代理名称")
    private String agentName;
    @Schema(description = "商户id")
    private Long merchantId;
    @Schema(description = "商户名")
    private String merchantName;


    @Schema(description = "接口发起")
    private Integer api;
    @Schema(description = "商户单号")
    private String  meraplid;


    @Schema(description = "申请单ID")
    private String applyid;
    @Schema(description = "要求附言")
    private String referencecode;
    @Schema(description = "要求账号")
    private String cardno;
    @Schema(description = "要求账户")
    private String cardname;

    private BigDecimal applyAmount;
    private String transferfid;
    private String otherfid;

    // 业务信息
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

    //
    @Schema(description = "通联状态")
    private String state;


    // 匹配状态(1)
    @Schema(description = "状态")
    private Integer status;

    // 通知下游(3)
    @Schema(description = "通知状态")
    private Integer notifyStatus;
    @Schema(description = "通知次数")
    private Integer notifyCount;

    // 通联来账白名单卡id
    private String cardId;
}