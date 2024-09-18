package io.renren.zin.service.exchange.dto;

import lombok.Data;

@Data
public class TExchangeLockRequest {
    private String applyid;  // 申请单号	applyid	String
    private String meraplid; // 申请单流水	meraplid	String
}
