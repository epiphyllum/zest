package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

// 预付费卡开卡任务
@Data
public class NewPrepaidJobReq {
    // 支持交易币种
    private String currency;
    private Integer authmaxcount;
    private BigDecimal authmaxamount;
    private String onlhkflag;
    private String begindate;
    private String enddate;

    private String cardexpiredate; // 有效期
    private String maincardno; // 主卡卡号maincardnoString30Y主卡产品类型为：021201:通华VPA电子卡的主卡卡号
    private Integer num; // 申请数量numString6O为空，默认申请1张，一次申请最大数量为10万
    private String email; // 接受卡片邮箱emailString50O为空，不发送VPA子卡信息，需自行到用卡平台下载
}
