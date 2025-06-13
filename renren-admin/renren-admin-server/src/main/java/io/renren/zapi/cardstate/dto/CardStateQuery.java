package io.renren.zapi.cardstate.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

// 卡状态查询 说明：用以查询卡申请, 是卡开通后的状态
@Data
@AllArgsConstructor
public class CardStateQuery {
    private String cardno;  // 卡号
    private String cardid;
}
