package io.renren.zapi.service.accountmanage.dto;

import lombok.Data;

@Data
public class MoneyAccountNotify {
    String cardno;       // 账户号码cardnoString30Y银行卡号
    String currency;     // 币种currencyString3Y
    String id;           // 银行账户ID
    String state;        // 状态stateString2Y0-待审核；1-审核通过；2-审核不通过；4-冻结；5-关闭；6-待复审
    String stateexplain; // 状态描述stateexplainString100O状态的描述
}
