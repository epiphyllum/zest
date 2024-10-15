package io.renren.zapi.allocate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MoneyApplyRes {
    String applyid;       // 申请单号
    String referencecode; // 打款附言
}
