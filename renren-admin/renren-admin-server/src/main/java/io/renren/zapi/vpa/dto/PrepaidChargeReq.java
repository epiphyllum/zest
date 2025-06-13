package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;


// 预付费子卡充值
@Data
public class PrepaidChargeReq {
    private String meraplid;
    private String cardno;
    private String cardid;
    private BigDecimal amount;
}
