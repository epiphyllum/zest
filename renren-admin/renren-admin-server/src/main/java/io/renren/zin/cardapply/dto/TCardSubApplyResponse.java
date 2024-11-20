package io.renren.zin.cardapply.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 子卡申请-response
@Data
@EqualsAndHashCode
public class TCardSubApplyResponse extends TResult {
    private String applyid;
}
