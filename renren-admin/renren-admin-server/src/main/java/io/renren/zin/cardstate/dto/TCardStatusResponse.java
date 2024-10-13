package io.renren.zin.cardstate.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TCardStatusResponse extends TResult {
    String cardno;  // 卡号	    	String	30	Y
    String cardstate;
}
