package io.renren.zin.service.card.dto;


import lombok.Data;

import java.math.BigDecimal;

// 卡申请单状态通知
@Data
public class TCardApplyNotify {
    private String applyid;             //		申请单号			32	Y
    private String trxtype;             //		交易类型			25	Y	见附录【交易类型】
    private String createtime;          //		申请时间			20	Y	 格式YYYY-MM-DD hh:mm:ss
    private BigDecimal fee;             //		申请费用			18,2	O
    private String feecurrency;         //		申请费用币种			3	O
    private String cardno;              //		卡号			30	O	申请成功后返回
    private String currency;            //		币种			3	O	申请成功后返回
    private String state;               //		申请单状态			2	Y	详细参考“附录——卡申请单状态”
    private String stateexplain;        //		状态说明			200	O
    private String trxcode;             //		交易类型			10	Y	详细参考“附录——交易类型”
    private BigDecimal securityamount;  //		担保金额			18,2	O
    private String securitycurrency;    //		担保金币种			3	O
}
