package io.renren.zin.b2b.dto;

import io.renren.zin.TResult;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
public class TFundMergeResponse extends TResult {
    private String applyid;
}
