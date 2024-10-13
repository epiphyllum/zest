package io.renren.zin.cardstate.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TCardUnfreezeResponse extends TResult {
    private String	meraplid;	 //
    private String	cardno;
}
