package io.renren.zin.service.sub.dto;

import lombok.Data;

// 子商户审核通知
@Data
public class TSubStatusNotify {
    private String meraplid;    // 跟踪号String	15	Y	相同注册的唯一标识
    private String cusid;       // 子商户号String	15	A	状态为审核成功时返回
    private String state;       // 状态String	2	Y	04：审核成功; 05：审核失败; 其他情况为空;

}
