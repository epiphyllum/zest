package io.renren.zapi.service.money.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class M2sReq {
    BigDecimal amount;
    Long subid;
    String meraplid;
}
