package io.renren.zapi.sub.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 自商户状态查询
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubQuery {
    Long subId;    // 跟踪号 32	Y	与原创建请求一致。与子商户编号二选一。
}
