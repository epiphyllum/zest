package io.renren.zin.service.deposit.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TDepositResponse extends TResult {
    String applyid;  // 申请单号	applyid	String	32	Y
}
