package io.renren.zin.service.accountmanage.dto;

import lombok.Data;

@Data
public class THistoryBalanceQuery {
    private String acctno; //	通联内部账户			通联内部账户账号，账号：“800*****”
    private String currency; //	币种			币种
    private String begday; //	记账起始日期			yyyyMMdd 固定+8时区的日期
    private String endday; //	记账结束日期			yyyyMMdd 固定+8时区的日期
}
