package io.renren.zin.service.umbrella.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TVaDepositApplyResponse extends TResult {
    String applyid;       // 申请单号
    String referencecode; // 打款附言
}
