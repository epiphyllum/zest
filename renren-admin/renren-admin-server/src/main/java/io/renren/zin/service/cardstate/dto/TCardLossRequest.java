package io.renren.zin.service.cardstate.dto;

import lombok.Data;


// 卡挂失申请
@Data
public class TCardLossRequest {
    String meraplid;// 申请单流水	String	32	Y
    String cardno;  // 卡号	    	String	30	Y
}
