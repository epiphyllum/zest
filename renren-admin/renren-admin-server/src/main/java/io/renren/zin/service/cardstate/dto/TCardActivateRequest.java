package io.renren.zin.service.cardstate.dto;

import lombok.Data;

// 实体卡激活-request
@Data
public class TCardActivateRequest {
    String	cardno; //	卡号
    String	cvv; //	CVV2
    String	expiredate; //	有效期
    String	idnumber; //	证件号码
    String	birthday; //	出生日期
}
