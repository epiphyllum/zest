package io.renren.zapi.allocate.dto;

import lombok.Data;

// 入金申请
@Data
public class MoneyApply {
    String meraplid;     // 申请单流水
    String currency;     // 币种
    String id;           // 银行账户id
}
