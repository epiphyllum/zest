package io.renren.zapi.cardmoney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 卡充值查询
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardChargeQuery {
    String applyid;
    String meraplid;
}
