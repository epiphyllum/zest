package io.renren.zapi.sub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 子商户审核通知
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubNotify {
    private Long subId;          // 子商户号
    private String cusname;      //
    private String state;        // 状态
}
