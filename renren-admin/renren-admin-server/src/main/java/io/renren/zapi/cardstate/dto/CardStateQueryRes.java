package io.renren.zapi.cardstate.dto;
import lombok.Data;

// 卡申请单查询应答
@Data
public class CardStateQueryRes {
    String cardno;             // 卡号
    String state;              // 申请单状态
    String stateexplain;       // 状态说明
}
