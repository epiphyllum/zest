package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 查询卡支付信息
@Data
@AllArgsConstructor
public class CardPayInfoReq {
    private String cardno; // 卡号
}
