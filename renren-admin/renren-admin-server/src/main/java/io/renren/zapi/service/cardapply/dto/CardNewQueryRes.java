package io.renren.zapi.service.cardapply.dto;

import lombok.Data;

import java.math.BigDecimal;

// 卡申请单查询应答
@Data
public class CardNewQueryRes {
    String meraplid;           // 申请单流水
    String applyid;            // 申请单号
    String createtime;         // 申请时间

    // 需要调整为我们的费用
    String fee;                // 申请费用
    String feecurrency;        // 申请费用币种
    String cardno;             // 卡号
    String currency;           // 币种
    String state;              // 申请单状态
    String stateexplain;       // 状态说明
    String trxcode;            // 交易类型
    String cardbusinesstype;   // 卡类型
    BigDecimal securityamount; // 担保金额
    String securitycurrency;   // 担保金币种
}
