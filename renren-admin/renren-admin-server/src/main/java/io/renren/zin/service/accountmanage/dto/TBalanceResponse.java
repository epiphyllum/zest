package io.renren.zin.service.accountmanage.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TBalanceResponse extends TResult {
    @Data
    public static class Item {
        private String acctno;    // 账号	通联内部虚拟账号和冻结账号。
        private String currency;  // 币种
        private BigDecimal balance;   // 余额
        private Integer type;      // 类型	0-虚拟户 1-冻结户
    }

    private List<Item> details;
}
