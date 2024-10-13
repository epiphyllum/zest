package io.renren.zin.umbrella.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TMoneyConfirm {
    String applyid;      // 申请单号
    BigDecimal amount;   // 金额
    String transferfid;  // 转账凭证
    String otherfid;     // 其他材料
}
