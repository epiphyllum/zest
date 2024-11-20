package io.renren.zapi.cardmoney.dto;

import lombok.Data;

import java.math.BigDecimal;

// 卡充值通知
@Data
public class CardChargeNotify {
    String meraplid;           // 申请单流水
    String applyid;            // 申请单号
    String cardno;             // 卡号
    BigDecimal amount;         //
    String currency;           // 币种
    BigDecimal merchantfee;     //
    BigDecimal merchantdeposit; //
    String state;              // 申请单状态
}

