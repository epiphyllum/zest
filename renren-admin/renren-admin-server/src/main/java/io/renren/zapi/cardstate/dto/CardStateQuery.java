package io.renren.zapi.cardstate.dto;


import lombok.Data;

// 卡状态查询
// 说明：用以查询卡申请
@Data
public class CardStateQuery {
    private String cardno;  // 卡号
}
