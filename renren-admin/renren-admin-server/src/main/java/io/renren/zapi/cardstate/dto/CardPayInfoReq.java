package io.renren.zapi.cardstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 查询卡支付信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPayInfoReq {
    private String cardno; // 卡号
}
