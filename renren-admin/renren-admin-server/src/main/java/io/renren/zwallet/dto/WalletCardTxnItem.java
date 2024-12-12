package io.renren.zwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

// 卡交易: 卡上的消费交易
@Data
public class WalletCardTxnItem {
    private String marketproduct;

    private Long walletId;

    // 主卡
    private String maincardno;

    // 业务字段
    private String logkv;
    private String trxcode;
    private String cardno;
    private String state;
    private String stateexplain;

    private BigDecimal amount;
    private String currency;

    private BigDecimal settleamount;
    private String settlecurrency;

    private String trxtime;
    private String trxdir;
    private String trxaddr;
    private String authcode;
    private String mcc;
    private String time;
}
