package io.renren.zin.service.exchange.dto;

import lombok.Data;

@Data
public class TExchangeQueryRequest {
    private	String meraplid;  // 申请单流水
    private String applyid;   // 申请单号
}
