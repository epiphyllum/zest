package io.renren.zin.b2b.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TB2bConfirmRequest {
    private String meraplid;
    private String applyid;
    private String extype;
    private String merfeeccy;
    private BigDecimal merfee;
}
