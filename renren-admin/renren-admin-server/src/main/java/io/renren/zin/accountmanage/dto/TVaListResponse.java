package io.renren.zin.accountmanage.dto;

import io.renren.zin.TResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode
public class TVaListResponse extends TResult {
    @Data
    public static class VaItem {
        private String id;         //	账户唯一标识
        private String accountno;  //	通联虚拟户
        private String currency;   //	币种
        private BigDecimal amount; //	收款户余额
        private String vaaccountno;//	收款账户号
    }
    private List<VaItem> accts;
}
