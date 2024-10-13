package io.renren.zin.cardstate.dto;


import lombok.Data;

// 销卡
@Data
public class TCardCancelRequest {
    String meraplid;// 申请单流水	String	32	Y
    String cardno;  // 卡号	    	String	30	Y
}
