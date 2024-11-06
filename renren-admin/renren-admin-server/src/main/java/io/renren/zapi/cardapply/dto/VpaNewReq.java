package io.renren.zapi.cardapply.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VpaNewReq {
    private String cycle;                // "场景类型"
    private String currency;             // "交易币种"
    private Integer authmaxcount;        // "最大笔数"
    private BigDecimal authmaxamount;    // "最大金额")
    private String onlhkflag;            // "仅限香港"
    private String begindate;            // @Schema(description = "期限开始")
    private String enddate;              // @Schema(description = "期限结束")
    private String naturalmonthflag;     //  @Schema(description = "是否自然月")
    private String naturalmonthstartday; // @Schema(description = "自然日")
    private String fixedamountflag;      // @Schema(description = "是否固定金额")

    private String state;                //  @Schema(description = "任务状态")
    private BigDecimal merchantFee;      // @Schema(description = "开卡费用")
    private BigDecimal feeCurrency;      // @Schema(description = "费用币种")

    private String maincardno;           // @Schema(description = "主卡")
    private Integer num;                 // @Schema(description = "开卡数量")
    private String email;                // @Schema(description = "邮箱")
    private String cardexpiredate;       // @Schema(description = "卡有效期")
    private String meraplid;             // @Schema(description = "商户发起的meraplid, 我们用id")
}
