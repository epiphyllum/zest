package io.renren.zin.b2b.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TEcoTransferRequest {
    private String meraplid;
    private String payeeaccountno;
    private String currency;
    private BigDecimal amount;
    private String islockamount;
    private String agrefid;
}
