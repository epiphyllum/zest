package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletInfo {
    @Data
    public static class WalletCard {
        private String cardno;
        private BigDecimal balance;
    }
    private List<WalletCard> usdCardList;  // 美元卡列表
    private List<WalletCard> hkdCardList;  // 港币卡列表
    private BigDecimal hkdBalance;   // 港币余额
    private BigDecimal usdBalance;   // 美金余额
}
