package io.renren.zin.exchange.dto;

import io.renren.zin.TResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TExchangeResponse extends TResult {
    private String applyid;  //  申请单号
}
