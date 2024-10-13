package io.renren.zapi.cardapply.dto;


import lombok.Data;

// 发卡申请单查询:
// 说明：用以查询卡申请
@Data
public class CardNewQuery {
    private String meraplid;// 申请单流水	String	32	Y
    private String cardno;  // 卡号	    	String	30	Y
}
