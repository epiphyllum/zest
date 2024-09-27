package io.renren.zapi.service.card.dto;

import lombok.Data;

// 查询卡支付信息
@Data
public class CardPayInfoRes {
    String cardno;     // 卡号
    String cvv;        // cvv2
    String expiredate; // 有效期
}
