package io.renren.zapi.vpa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SetQuotaReq {
    String meraplid;
    String cardno;
    String cardid;
    BigDecimal authmaxamount;
    Integer authmaxcount;
}
