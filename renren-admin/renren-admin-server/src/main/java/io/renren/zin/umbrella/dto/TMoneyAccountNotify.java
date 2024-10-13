package io.renren.zin.umbrella.dto;

import lombok.Data;

// 8005-银行账户状态通知
@Data
public class TMoneyAccountNotify {
    String cardno;       // 账户号码, 银行卡号
    String currency;     // 币种
    String id;           // 银行账户
    String state;        // 状态 0-待审核；1-审核通过；2-审核不通过；4-冻结；5-关闭；6-待复审
    String stateexplain; // 状态描述
}
