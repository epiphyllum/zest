package io.renren.zin.service.cardapply.dto;

import lombok.Data;

// 3008 - 变更VPA子卡场景信息
@Data
public class TCardUpdateScene {
    String currency;  // 允许交易币种 currencyString100O支持多币种，多币种时以逗号(,)隔开；为空时，默认全币种 ；见附录【币种信息】
    String cardno; //
    String authmaxcount; //    最大授权笔数authmaxcountString10C使用场景为期限时填入，填入数字字符串，为“0”时默认不限制
    String authmaxamount; //    最大交易授权金额authmaxamountString10C使用场景为期限时填入，填入数字字符串，保留2位小数点
}
