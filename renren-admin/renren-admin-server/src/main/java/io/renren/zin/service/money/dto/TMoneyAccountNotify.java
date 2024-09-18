package io.renren.zin.service.money.dto;

import lombok.Data;

@Data
public class TMoneyAccountNotify {
    String cardno; // 账户号码cardnoString30Y银行卡号
    String currency; //        币种currencyString3Y
    String id; // 银行账户IDidString15Y
    String state; // 状态stateString2Y0-待审核；1-审核通过；2-审核不通过；4-冻结；5-关闭；6-待复审
    String stateexplain; //        状态描述stateexplainString100O状态的描述
}
