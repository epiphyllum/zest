package io.renren.zin.service.internal.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TAccountListResponse extends TResult {

    @Data
    public static class Item {
        private String	accountno	; //	120	Y	通联虚拟户
        private String	currency	; //	3	Y	币种
        private BigDecimal amount	; //	18,2	Y	收款户余额
        private String	vaaccountno	; //	30	O	收款账户号
    }
    private List<Item> accts;
}
