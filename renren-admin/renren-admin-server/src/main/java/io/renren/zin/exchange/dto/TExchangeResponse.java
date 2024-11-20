package io.renren.zin.exchange.dto;

import io.renren.zin.TResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
public class TExchangeResponse extends TResult {
    private String applyid;  //  申请单号
}
