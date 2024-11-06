package io.renren.zapi.cardapply.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VpaNewNotify {
    String meraplid;              // 商户单号
    String applyid;               // 申请单号
    String state;                 // 状态
}
