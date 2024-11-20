package io.renren.zin.cardstate.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TCardUnfreezeResponse extends TResult {
    private String	meraplid;	 //
    private String	cardno;
}
