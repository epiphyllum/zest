package io.renren.zin.cardmoney.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TDepositResponse extends TResult {
    String applyid;  // 申请单号	applyid	String	32	Y
}
