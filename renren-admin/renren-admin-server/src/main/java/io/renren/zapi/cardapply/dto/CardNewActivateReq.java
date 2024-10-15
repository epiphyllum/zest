package io.renren.zapi.cardapply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 实体卡激活-request
@Data
@AllArgsConstructor
public class CardNewActivateReq {
    String	cardno; //	卡号
    String	cvv; //	CVV2
    String	expiredate; //	有效期
    String	idnumber; //	证件号码
    String	birthday; //	出生日期
}
