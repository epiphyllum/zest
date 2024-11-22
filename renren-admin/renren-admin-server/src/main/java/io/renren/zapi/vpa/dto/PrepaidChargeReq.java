package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrepaidChargeReq {
    private String meraplid;
    private String cardno;
    private BigDecimal amount;
}
