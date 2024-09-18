package io.renren.zin.service.cardstatus.dto;

import lombok.Data;

// 卡状态变更通知
@Data
public class TCardChangeNotify {
    private String cardno;  //  卡号
    private String currency;  //币种
    private String cardtype;   //卡形式
    private String cardstate;   //卡状态
}
