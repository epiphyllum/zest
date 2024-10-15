package io.renren.zapi.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 锁汇
@Data
@AllArgsConstructor
public class ExchangeLockReq {
    private String applyid;  // 申请单号	applyid	String
    private String meraplid; // 申请单流水	meraplid	String
}
