package io.renren.zapi.exchange.dto;

import lombok.Data;

// 锁汇
@Data
public class ExchangeLockReq {
    private String applyid;  // 申请单号	applyid	String
    private String meraplid; // 申请单流水	meraplid	String
}
