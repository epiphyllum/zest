package io.renren.zapi.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyConfirm {
    String applyid;     // 申请单号
    BigDecimal amount;  // 金额
    String transferfid; // 转账凭证
    String otherfid;    // 其他材料
}
