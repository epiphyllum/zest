package io.renren.zin.exchange.dto;

import io.renren.zin.TResult;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TExchangeLockResponse extends TResult {
    private String applyid;          // 申请单号
    private BigDecimal amount;       // 交易金额
    private String feecurrency;      // 交易币种
    private BigDecimal settleamount; // 结算金额
    private String settlecurrency;   // 结算币种
    private BigDecimal fxrate;       // 汇率
    private BigDecimal fee;          // 手续费
}
