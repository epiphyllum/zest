package io.renren.zin.service.exchange.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TExchangeResponse extends TResult {
    private String applyid;  //  申请单号
}
