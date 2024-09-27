package io.renren.zapi.service.accountmanage.dto;

import lombok.Data;

@Data
public class MoneyAccountStatusNotify {
    String cardno;       // 账户号码cardnoString30Y银行卡号
    String currency;     // 账户名称cardnameString16Y
    String id;
    String state;
    String stateexplain;
}
