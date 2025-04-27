package io.renren.zin.b2b.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TFundMergeRequest {
    private String meraplid;
    private String payeemerid;
    private String currency;
    private BigDecimal amount;
}
