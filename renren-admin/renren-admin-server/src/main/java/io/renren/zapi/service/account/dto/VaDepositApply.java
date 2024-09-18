package io.renren.zapi.service.account.dto;

import lombok.Data;



// 入金申请
@Data
public class VaDepositApply {
    String meraplid; // 申请单流水meraplidString32Y 客户自己生成，保持唯一
    String currency; // 币种currencyString3Y收款户币种
    String id;       // 银行账户idString30Y付款银行账户id，新增银行账户成功后响应
}
