package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包详情
@Data
public class WalletInfo {
    private String hkdLevel;         // 港币账户等级
    private String usdLevel;         // 美元账户等级

    // 法币钱包
    private BigDecimal hkdBalance;   // 港币余额
    private BigDecimal usdBalance;   // 美金余额
    // 数字货币
    private BigDecimal usdtBalance;  // USDT余额
    private BigDecimal usdcBalance;  // USDC余额
    private BigDecimal btcBalance;   // BTC余额
    private BigDecimal ethBalance;   // ETH余额

    private BigDecimal usdEstimate;  // 美元估值
}
