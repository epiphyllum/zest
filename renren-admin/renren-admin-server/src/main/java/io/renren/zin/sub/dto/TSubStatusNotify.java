package io.renren.zin.sub.dto;

import lombok.Data;

// 子商户审核通知
// {"ctid":"1842166361762832385","cusid":"665000000008412","cusname":"测试商户","meraplid":"1842166361762832385","state":"04","stateexplain":"正常"}
@Data
public class TSubStatusNotify {
    private String ctid;
    private String cusid;        // 子商户号
    private String meraplid;     // 跟踪号
    private String cusname;
    private String state;        // 状态
    private String stateexplain; //
}
