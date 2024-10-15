package io.renren.zapi.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class S2mReq {
    String currency;
    BigDecimal amount;
    String meraplid;
    Long subId;
}
