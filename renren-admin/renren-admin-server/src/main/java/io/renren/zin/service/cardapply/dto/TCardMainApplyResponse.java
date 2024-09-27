package io.renren.zin.service.cardapply.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

// 主卡申请-response
@Data
public class TCardMainApplyResponse extends TResult {
   private String applyid;
}