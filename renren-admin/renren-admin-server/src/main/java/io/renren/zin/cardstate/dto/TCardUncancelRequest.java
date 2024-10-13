package io.renren.zin.cardstate.dto;


import lombok.Data;

// 销卡解除
@Data
public class TCardUncancelRequest {
    String meraplid;// 申请单流水	String	32	Y
    String cardno;  // 卡号	    	String	30	Y
}
