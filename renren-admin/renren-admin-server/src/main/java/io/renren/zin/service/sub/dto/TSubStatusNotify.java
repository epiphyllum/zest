package io.renren.zin.service.sub.dto;

import lombok.Data;

// 子商户审核通知
@Data
public class TSubStatusNotify {
    private String meraplid;    // 跟踪号
    private String cusid;       // 子商户号
    private String state;       // 状态

}
