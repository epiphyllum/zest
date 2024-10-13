package io.renren.zin.cardmoney.dto;

import lombok.Data;

import java.math.BigDecimal;

// 3101 - 提取保证金
@Data
public class TWithdrawRequest {
    String meraplid;   //申请单流水
    String cardno;     //卡号
    String payeeid;    //保证金提现账户
    BigDecimal amount; //保证金提现金额
}





