package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewShareJobReq {
    private String meraplid;
    private Long subId;
    // 支持交易币种
    private String currency;               // 支持交易币种, 逗号分割
    private String cycle;                  // 场景类型:
    private Integer authmaxcount;          // 最大授权笔数
    private BigDecimal authmaxamount;      // 最大授权金额
    private String onlhkflag;              // 是否仅限香港
    private String begindate;              // 期限卡开始日期
    private String enddate;                // 期限卡结束日期
    private String naturalmonthflag;       // 是否自然月
    private String naturalmonthstartday;   // 自然月开始日期
    private String fixedamountflag;        // 是否单次定额

    private String cardexpiredate;         // 有效期
    private String maincardno;             // 主卡卡号
    private Integer num;                   // 申请数量
    private String email;                  // 接受卡片邮箱
}
