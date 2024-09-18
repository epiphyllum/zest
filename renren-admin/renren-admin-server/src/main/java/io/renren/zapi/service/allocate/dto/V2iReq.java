package io.renren.zapi.service.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class V2iReq {
   BigDecimal amount;
   String meraplid;
}
