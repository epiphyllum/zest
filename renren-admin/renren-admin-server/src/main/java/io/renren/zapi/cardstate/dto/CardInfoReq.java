package io.renren.zapi.cardstate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 查询卡支付信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoReq {
    private String cardno;        // 卡号
    private String cardid;
}
