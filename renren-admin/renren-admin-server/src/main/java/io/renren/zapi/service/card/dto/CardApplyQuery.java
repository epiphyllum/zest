package io.renren.zapi.service.card.dto;


import lombok.Data;

// 卡申请单查询:
// 说明：用以查询主卡/子卡申请、缴纳保证金、提取保证金、卡注销申请单当前处理进度。
@Data
public class CardApplyQuery {
    private String meraplid;// 申请单流水	String	32	Y
    private String cardno;  // 卡号	    	String	30	Y
}
