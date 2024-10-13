package io.renren.zin.exchange.dto;

import lombok.Data;


// 2000 - 换汇申请
@Data
public class TExchangeQueryRequest {
    private	String meraplid;  // 申请单流水
    private String applyid;   // 申请单号
}
