package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTxnItem {
    private String currency;       // 结算币种
    private BigDecimal stlAmount;  // 结算金额
    private String txnCode;        // 交易代码
    private BigDecimal txnAmount;  // 交易金额
    private String usdtAddress;    // usdt收款地址
}
