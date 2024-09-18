package io.renren.zapi.service.internal.dto;

import lombok.Data;

// 入金账户查询:
// 1. 如果给了currency就是查询指定入金账户，
// 2. 如果没给， 就是查询所有入金账户
@Data
public class InMoneyAccountReq {
    String currency;
}
