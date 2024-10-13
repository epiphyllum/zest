package io.renren.zapi.cardstate.dto;

import lombok.Data;

// 查询卡支付信息
@Data
public class CardPayInfoReq {
    private String cardno; // 卡号
}
