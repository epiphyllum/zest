package io.renren.zin.cardapply.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 主卡申请-response
@Data
@EqualsAndHashCode
public class TCardMainApplyResponse extends TResult {
   private String applyid;
}