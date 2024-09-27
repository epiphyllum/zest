package io.renren.zin.service.umbrella.dto;

import lombok.Data;

// 8005 - 银行账户查询
@Data
public class TMoneyAccountQuery {
    String id;
    String cardno;
    String currency;
}
