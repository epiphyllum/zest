package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 查询卡支付信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPayInfoRes {
    String cardno;     // 卡号
    String cvv;        // cvv2
    String expiredate; // 有效期
}
