package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrepaidWithdrawReq {
    private String meraplid;
    private String cardno;
    private String cardid;
    private BigDecimal amount;
}
