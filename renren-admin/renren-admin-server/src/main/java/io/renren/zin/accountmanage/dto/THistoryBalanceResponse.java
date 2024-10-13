package io.renren.zin.accountmanage.dto;

import io.renren.zin.TResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class THistoryBalanceResponse extends TResult {

    @Data
    public static class Item {
        private String memacctname;    //	户名	Y	账户名称
        private String memacct;        //	账号	Y	充值到账账户账号
        private String acctno;         //	通联内部账户	O	通联内部账户账号，账号：“800*****”
        private String currency;       //	币种	Y	币种
        private BigDecimal openingbal; //	期初余额	Y	日期当天0点账户余额
        private BigDecimal closingbal; //	期末余额	Y	日期当天24点账户余额
        private BigDecimal gmv;        //	期间发生额	Y	日期当天0点至24点该账户变更总额
        private String periodday;      //	日期	Y	yyyyMMdd 固定+8时区的日期
    }

    private List<Item> details; //
    private String acctno;      //	通联内部账户			通联内部账户账号，账号：“800*****”
    private String currency;    //	币种			币种
    private String begday;      //	记账起始日期			yyyyMMdd 固定+8时区的日期
    private String endday;      //	记账结束日期			yyyyMMdd 固定+8时区的日期
}
