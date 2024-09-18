package io.renren.zin.service.card.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

import java.math.BigDecimal;

// 卡申请单查询应答
@Data
public class TCardApplyResponse extends TResult {
    String meraplid;           // 申请单流水	meraplid	String	32	Y	  	String	32	Y	 
    String applyid;        //   申请单号	applyid	String	32	Y	   //	String	32	Y	 
    String createtime;     //   申请时间	createtime	String	20	Y	   //	String	20	Y	 
    String fee;            //   申请费用	fee	Number	18,2	O	   //	Number	18,2	O	 
    String feecurrency;        // 申请费用币种	feecurrency	String	3	O		String	3	O
    String cardno;          //   卡号	cardno	String	30	O	申请成功后返回 //	String	30	O	申请成功后返回
    String currency;        //   币种	currency	String	3	O	申请成功后返回 //	String	3	O	申请成功后返回
    String state;              // 申请单状态	state	String	2	Y	详细参考“附录——卡申请单状态”	String	2	Y	详细参考“附录——卡申请单状态”
    String stateexplain;   //   状态说明	stateexplain	String	200	O	  //	String	200	O
    String trxcode;        //   交易类型	trxcode	String	10	Y	详细参考“附录——交易类型”  //	String	10	Y	详细参考“附录——交易类型”
    String cardbusinesstype;   // 卡类型	cardbusinesstype	String	1	Y	1：主卡2：子卡	String	1	Y	1：主卡2：子卡
    BigDecimal securityamount;  //  担保金额	securityamount	Number	18,2	O	   //	Number	18,2	O	 
    String securitycurrency;   // 担保金币种	securitycurrency	String	3	O		String	3	O
}
