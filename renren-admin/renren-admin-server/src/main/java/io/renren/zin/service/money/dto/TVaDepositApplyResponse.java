package io.renren.zin.service.money.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TVaDepositApplyResponse extends TResult {
    String applyid; // 申请单号applyidString32Y
    String referencecode; // 打款附言referencecodeString20YReference Code
}
