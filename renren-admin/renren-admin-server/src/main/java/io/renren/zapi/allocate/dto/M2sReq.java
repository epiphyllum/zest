package io.renren.zapi.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class M2sReq {
    String currency;
    BigDecimal amount;
    Long subId;
    String meraplid;
}
