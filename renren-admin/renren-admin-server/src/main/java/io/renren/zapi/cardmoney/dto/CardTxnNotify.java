package io.renren.zapi.cardmoney.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardTxnNotify {
    private Long merchantId;
    private Long subId;

    private String respcode;
    private String respmsg;

    // 业务字段
    private String logkv;
    private String trxcode;
    private String cardno;
    private String state;
    private String stateexplain;
    private BigDecimal amount;
    private String currency;
    private String trxtime;
    private String trxdir;
    private String trxaddr;
    private String authcode;
    private String mcc;
}
