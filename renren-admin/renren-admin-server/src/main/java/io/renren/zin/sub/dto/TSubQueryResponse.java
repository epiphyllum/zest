package io.renren.zin.sub.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TSubQueryResponse extends TResult {
    private String ctid;    // 相同注册的唯一标识
    private String cusid;   // 通联子商户号
    private String cusname; // 客户名
    private String state;   // 状态
}
