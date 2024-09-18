package io.renren.zapi.service.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class I2vReq {
   BigDecimal amount;
   String meraplid;
}
