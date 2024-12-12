package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包充值 | 提现记录
@Data
public class WalletTxnItem {
    private String txnCode;        // 交易代码
    private String currency;       // 结算币种
    private BigDecimal stlAmount;  // 结算金额

    private String payCurrency;    // 交易币种
    private BigDecimal payAmount;  // 交易金额
}
