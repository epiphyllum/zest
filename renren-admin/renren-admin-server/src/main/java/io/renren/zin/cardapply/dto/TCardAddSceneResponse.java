package io.renren.zin.cardapply.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 3008 - 变更VPA子卡场景信息
@Data
@EqualsAndHashCode
public class TCardAddSceneResponse extends TResult {
    String sceneid;
}
