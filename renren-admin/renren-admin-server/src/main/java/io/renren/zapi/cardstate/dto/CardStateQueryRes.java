package io.renren.zapi.cardstate.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

// 卡申请单查询应答
@Data
@AllArgsConstructor
public class CardStateQueryRes {
    String cardno;             // 卡号
    String state;              // 申请单状态
}
