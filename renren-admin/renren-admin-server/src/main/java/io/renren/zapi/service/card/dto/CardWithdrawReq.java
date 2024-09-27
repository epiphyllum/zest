package io.renren.zapi.service.card.dto;

import lombok.Data;

import java.math.BigDecimal;

// 提取保证金
@Data
public class CardWithdrawReq {
    String meraplid;   //申请单流水
    String cardno;     //卡号
    String payeeid;    //保证金提现账户
    BigDecimal amount; //保证金提现金额
}





