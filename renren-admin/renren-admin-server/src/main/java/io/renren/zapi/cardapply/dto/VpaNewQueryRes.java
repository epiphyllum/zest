package io.renren.zapi.cardapply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// 卡申请单查询应答
@Data
public class VpaNewQueryRes {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        String cvv;
        String expiredate;
        String cardno;
    }
    String meraplid;           // 申请单流水
    String applyid;            // 申请单号
    BigDecimal merchantfee;    // 申请费用  todo: 调整为我们的费用
    String feecurrency;        // 申请费用币种
    String state;              // 申请单状态
    List<Item> items;          // cvv 有效期 卡号
}
