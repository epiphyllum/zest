package io.renren.zapi.service.allocate.dto;

import lombok.Data;



// 入金申请
@Data
public class VaDepositApply {
    String meraplid; // 申请单流水
    String currency; // 币种
    String id;       // 银行账户id
}
