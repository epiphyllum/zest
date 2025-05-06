package io.renren.zin.b2b.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TB2bConfirmResponse extends TResult {
    private String meraplid;
    private String applyid;
    private String extype;
}
