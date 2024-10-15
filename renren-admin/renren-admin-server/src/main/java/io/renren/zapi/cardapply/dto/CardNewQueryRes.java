package io.renren.zapi.cardapply.dto;

import lombok.Data;

import java.math.BigDecimal;

// 卡申请单查询应答
@Data
public class CardNewQueryRes {
    String meraplid;           // 申请单流水
    String applyid;            // 申请单号
    BigDecimal merchantfee;        // 申请费用  todo: 调整为我们的费用
    String feecurrency;        // 申请费用币种
    String cardno;             // 卡号
    String currency;           // 币种
    String state;              // 申请单状态
}
