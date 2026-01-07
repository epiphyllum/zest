package io.renren.zapi.cardmoney.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardTxnNotify {
    private Long merchantId;
    private Long subId;

    // 业务字段
    private String logkv;
    private String trxcode;
    private String cardid;
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

    private String acqcountry;// todo 3
    private String respcode;// todo  4
    private String respmsg;// todo  80
    private String dsflag;  // todo  1
}
