package io.renren.zin.cardmoney.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class TCardBalanceResponse extends TResult {
    private String cardno;        //卡号
    private String currency;    //   币种
    private BigDecimal balance;     //  余额
    private BigDecimal authamount;
}
