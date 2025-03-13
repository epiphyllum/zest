package io.renren.zapi.vpa.dto;

import lombok.Data;

// 预付费子卡充值应答
@Data
public class PrepaidChargeRes {
    private String meraplid;
    private String state;
}
