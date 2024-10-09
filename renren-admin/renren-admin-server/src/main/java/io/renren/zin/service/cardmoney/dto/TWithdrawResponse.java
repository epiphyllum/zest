package io.renren.zin.service.cardmoney.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TWithdrawResponse extends TResult {
    String applyid;  // 申请单号	applyid	String	32	Y
}
