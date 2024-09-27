package io.renren.zin.service.cardmoney.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TCardBalanceResponse extends TResult {
    private String cardno;        //卡号
    private String currency;    //   币种
    private BigDecimal balance;     //  余额
}
