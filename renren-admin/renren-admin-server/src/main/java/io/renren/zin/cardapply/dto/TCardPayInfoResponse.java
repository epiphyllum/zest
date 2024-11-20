package io.renren.zin.cardapply.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 查询卡支付信息
@Data
@EqualsAndHashCode
public class TCardPayInfoResponse extends TResult {
    String cardno; // 卡号	String	30	Y
    String cvv; // cvv2	String	3	Y	加密字段
    String expiredate; // 有效期	String	4	Y	加密字段
}
