package io.renren.zapi.cardmoney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 卡充值查询
@AllArgsConstructor
@Data
public class CardChargeQuery {
    String applyid;
    String meraplid;
}
