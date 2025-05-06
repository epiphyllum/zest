package io.renren.zin.b2b.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TVaMoneyNotify {
    private Long merchantId;
    private String applyid;
    private String nid;
    private String bid;
    private String acctno;
    private String currency;
    private String trxcod;
    private BigDecimal amount;
    private String time;
    private String payeraccountname;
    private String payeraccountno;
    private String payeraccountbank;
    private String payeraccountcountry;
    private String ps;
}
