package io.renren.zin.service.cardstatus.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TCardStatusResponse extends TResult {
    String cardno;  // 卡号	    	String	30	Y
    String cardstate;
}
