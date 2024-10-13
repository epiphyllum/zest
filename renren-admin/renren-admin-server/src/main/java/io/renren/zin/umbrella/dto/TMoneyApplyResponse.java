package io.renren.zin.umbrella.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TMoneyApplyResponse extends TResult {
    String applyid;       // 申请单号
    String referencecode; // 打款附言
}
