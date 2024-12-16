package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 钱包卡信息
@Data
public class WalletCard {
    private String cardno;             // 卡号
    private BigDecimal balance;        // 余额
    private String cvv;
    private String expiredate;
    private String marketproduct;
    private String name;
    private String surname;
}
