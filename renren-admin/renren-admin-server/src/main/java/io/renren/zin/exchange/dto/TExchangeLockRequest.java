package io.renren.zin.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TExchangeLockRequest {
    private String applyid;  // 申请单号
    private String meraplid; // 申请单流水
}
