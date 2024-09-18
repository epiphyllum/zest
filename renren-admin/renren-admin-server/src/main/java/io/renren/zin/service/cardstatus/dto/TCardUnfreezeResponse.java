package io.renren.zin.service.cardstatus.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TCardUnfreezeResponse extends TResult {
    private String	meraplid;	 //
    private String	cardno;
}
