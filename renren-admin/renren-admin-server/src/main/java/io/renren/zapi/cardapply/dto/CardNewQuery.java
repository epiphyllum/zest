package io.renren.zapi.cardapply.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

// 发卡申请单查询, 说明：用以查询卡申请
@Data
@AllArgsConstructor
public class CardNewQuery {
    private String meraplid;   // 商户单号
    private String applyid;    // 申请单号
}
