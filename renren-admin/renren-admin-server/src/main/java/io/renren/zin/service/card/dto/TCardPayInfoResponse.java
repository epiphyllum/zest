package io.renren.zin.service.card.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

// 查询卡支付信息
@Data
public class TCardPayInfoResponse extends TResult {
    String cardno; // 卡号	String	30	Y
    String cvv; // cvv2	String	3	Y	加密字段
    String expiredate; // 有效期	String	4	Y	加密字段
}
