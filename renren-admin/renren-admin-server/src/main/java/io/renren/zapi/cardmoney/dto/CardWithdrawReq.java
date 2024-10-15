package io.renren.zapi.cardmoney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

// 提取保证金
@Data
public class CardWithdrawReq {
    Long subId;      // 子商户ID;
    String meraplid;   //申请单流水
    String cardno;     //卡号
    BigDecimal amount; //保证金提现金额
}





