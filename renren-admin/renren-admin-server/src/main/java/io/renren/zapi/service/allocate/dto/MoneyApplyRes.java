package io.renren.zapi.service.allocate.dto;

import lombok.Data;

@Data
public class MoneyApplyRes {
    String applyid;       // 申请单号
    String referencecode; // 打款附言
}
