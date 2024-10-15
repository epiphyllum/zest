package io.renren.zapi.account.dto;

import lombok.Data;

@Data
public class MoneyAccountNotify {
    String id;             // 账户ID
    String cardno;         // 账户号码cardnoString30Y银行卡号
    String cardname;       // 账户名称cardnameString16Y
    String bankname;       // 银行名称banknameString16Y
    String currency;       //
    String state;          //
}
