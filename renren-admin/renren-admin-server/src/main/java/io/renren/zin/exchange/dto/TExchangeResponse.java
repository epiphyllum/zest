package io.renren.zin.exchange.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TExchangeResponse extends TResult {
    private String applyid;  //  申请单号
}
