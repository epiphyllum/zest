package io.renren.zin.cardapply.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 3008 - 变更VPA子卡场景信息
@Data
@EqualsAndHashCode
public class TCardQuerySceneResponse extends TResult {

    String scenename; // 场景名称 scenenameString50Y 保证唯一
    String cycle;     // 使用场景 cycleString1Y1:期限，2:周期，3:单次
    String currency;  // 允许交易币种 currencyString100O支持多币种，多币种时以逗号(,)隔开；为空时，默认全币种 ；见附录【币种信息】
    String onlhkflag; // 是否仅支持香港地区交易onlhkflagString1OY:是，N：否

    //
    String authmaxcount; //    最大授权笔数authmaxcountString10C使用场景为期限时填入，填入数字字符串，为“0”时默认不限制
    String authmaxamount; //    最大交易授权金额authmaxamountString10C使用场景为期限时填入，填入数字字符串，保留2位小数点

    /**
     * 以下字段使用场景为期限时填入
     */
    String begindate; //    使用期限起始begindateString10C使用场景为期限时填入，YYYY-MM-DD
    String endate; //    使用期限结束enddateString10C使用场景为期限时填入，YYYY-MM-DD

    /**
     * 以下字段使用场景为周期时填入
     */
    String naturalmonthflag; //    周期设置-自然月naturalmonthflagString1C使用场景为周期时填入,Y:是，N：否
    String naturalmonthstartday; //    周期设置-自然日设置naturalmonthstartdayString2C周期设置-自然月为否是填入，1-28之间

    /**
     * 以下字段使用场景为单次时填入
     */
    String fixedamountflag;//    是否固定消费金额fixedamountflagString1C使用场景为单次时填入,Y:是，N：否

}
