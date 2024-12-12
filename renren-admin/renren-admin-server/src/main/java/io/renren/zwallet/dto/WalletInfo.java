package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包详情
@Data
public class WalletInfo {
    private String hkdLevel;               // 账户等级
    private String usdLevel;               // 账户等级
    private BigDecimal hkdBalance;         // 港币余额
    private BigDecimal usdBalance;         // 美金余额
    private BigDecimal usdEstimate;        // 估值
}
