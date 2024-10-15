package io.renren.zapi.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 换汇申请应答
@Data
@AllArgsConstructor
public class ExchangeRes {
    private String applyid;  //  申请单号
}
