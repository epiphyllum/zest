package io.renren.zin.cardapply.dto;


import lombok.Data;

import java.math.BigDecimal;

// 卡申请单状态通知
@Data
public class TCardApplyNotify {
    private String applyid;             //	申请单号
    private String createtime;          //	申请时间, 格式YYYY-MM-DD hh:mm:ss

    private BigDecimal fee;             //	申请费用	18,2
    private String feecurrency;         //	申请费用币种

    private String cardno;              //	卡号			30	O	申请成功后返回
    private String amount;              //  申请金额
    private String currency;            //	币种			3	O	申请成功后返回

    private String state;               //	申请单状态			2	Y	详细参考“附录——卡申请单状态”
    private String stateexplain;        //	状态说明			200	O
    private String trxcode;             //	交易类型			10	Y	详细参考“附录——交易类型”

    private String cardbusinesstype;    // 主卡/子卡标志  1: 主卡  2: 子卡

    private BigDecimal securityamount;  //	担保金额			18,2	O
    private String securitycurrency;    //	担保金币种			3	O
}
