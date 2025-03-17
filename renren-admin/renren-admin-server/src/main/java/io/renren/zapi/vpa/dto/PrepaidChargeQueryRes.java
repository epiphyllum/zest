package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

// 预付费子卡充值
@Data
public class PrepaidChargeQueryRes {
    private String meraplid;
    private BigDecimal quota;  // 额度
    private String state;
}
