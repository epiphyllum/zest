package io.renren.zapi.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeQuery {
    private	String meraplid;  // 申请单流水
    private String applyid;   // 申请单号
}
