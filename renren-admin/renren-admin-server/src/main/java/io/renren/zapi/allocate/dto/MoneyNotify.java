package io.renren.zapi.allocate.dto;

import lombok.Data;

import java.math.BigDecimal;

// 入金通知:  我们只需要告诉客户多少钱入账
@Data
public class MoneyNotify {
    String currency;            // 币种
    BigDecimal amount;          // 金额

    String payeraccountno;      //
    String payeraccountname;    //	打款方姓名	付款方账户名称
    String payeraccountbank;    //	打款方银行号
    String payeraccountcountry; //	打款方国家
    String ps;                  //	附言
}

