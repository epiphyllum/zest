package io.renren.zin.sub.dto;


import lombok.Data;

// 自商户状态查询
@Data
public class TSubQuery {
    String meraplid;    // 跟踪号 32	Y	与原创建请求一致。与子商户编号二选一。
}
