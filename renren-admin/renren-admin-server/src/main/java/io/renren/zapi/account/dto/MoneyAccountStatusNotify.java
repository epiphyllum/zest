package io.renren.zapi.account.dto;

import lombok.Data;

@Data
public class MoneyAccountStatusNotify {
    String cardno;       // 账户号码|银行卡号
    String currency;     // 币种
    String id;           // 银行卡ID
    String state;        // 状态
    String stateexplain; // 状态描述
}
