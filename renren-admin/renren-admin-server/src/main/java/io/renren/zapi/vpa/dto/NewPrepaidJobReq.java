package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

// 预付费卡开卡任务
@Data
public class NewPrepaidJobReq {
    private String meraplid;
    // 支持交易币种
    private String currency;             // 支持交易币种, 逗号分隔
    private Integer authmaxcount;        // 最大授权交易笔数
    private BigDecimal authmaxamount;    // 最大授权交易额
    private String onlhkflag;            // 是否仅限香港交易
    private String begindate;            // 期限开始日期
    private String enddate;              // 期限结束日期

    private String cardexpiredate; // 有效期
    private String maincardno;     // 主卡卡号
    private Integer num;           // 申请数量
    private String email;          // 接受卡片邮箱
}
