package io.renren.zin.b2b.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TCommonQueryResponse extends TResult {
    private String applyid;
    private String state;
    private String stateExplain;
}
