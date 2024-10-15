package io.renren.zapi.cardapply.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardNewNotify {
    String meraplid;              // 商户单号
    String applyid;               // 申请单号
    BigDecimal merchantFee;       // 商户费用
    String feecurrency;           // 费用币种
    String state;                 // 状态
}
