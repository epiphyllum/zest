package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_exchange
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@Schema(description = "j_exchange")
public class JExchangeDTO implements Serializable {
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

    //
    @Schema(description = "完成日期")
    private Date statDate;

    // id相关(4)
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理名")
    private String agentName;
    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户名")
    private String merchantName;
    private Integer api;

    // 业务字段 5
    @Schema(description = "申请单流水")
    private String meraplid;
    @Schema(description = "到账币种")
    private String payeeccy;
    @Schema(description = "卖出币种")
    private String payerccy;
    @Schema(description = "锁定方")
    private String lockamountflag;
    @Schema(description = "金额")
    private BigDecimal amount;

    //  返回(2)
    @Schema(description = "申请单号")
    private String applyid;
    @Schema(description = "状态")
    private String state;

    // 锁汇情况(5)
    @Schema(description = "fee currency")
    private String feecurrency;
    @Schema(description = "结算金额")
    private BigDecimal settleamount;
    @Schema(description = "结算币种")
    private String settlecurrency;
    @Schema(description = "汇率")
    private BigDecimal fxrate;
    @Schema(description = "手续费")
    private BigDecimal fee;

    // 执行情况(4)
    @Schema(description = "锁汇方式")
    private String extype;
    @Schema(description = "金额")
    private BigDecimal stlamount;
    @Schema(description = "费用")
    private BigDecimal exfee;
    @Schema(description = "汇率")
    private BigDecimal exfxrate;
}