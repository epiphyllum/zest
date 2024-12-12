package io.renren.zapi.cardmoney.dto;

import lombok.Data;

import java.math.BigDecimal;

// 缴纳保证金
@Data
public class CardChargeReq {
    Long subId;             // 子商户ID
    String meraplid;        // 申请单流水
    String cardno;          // 卡号
    BigDecimal amount;      // 缴纳金额

    String payeeaccount;    // 交易对手
    String procurecontent;  // 采购内容
    String agmfid;          // 保证金对应合同协议
}
