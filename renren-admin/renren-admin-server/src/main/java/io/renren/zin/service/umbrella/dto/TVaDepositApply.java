package io.renren.zin.service.umbrella.dto;

import lombok.Data;


// 8000 - 入金申请
@Data
public class TVaDepositApply {
    String meraplid; // 申请单流水
    String currency; // 币种
    String id;       // 银行账户id
}
