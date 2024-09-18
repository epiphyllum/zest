package io.renren.zin.service.va.dto;

import lombok.Data;

import java.math.BigDecimal;


// 入账通知
@Data
public class TMoneyInNotify {
    private String		nid	;//	32	通知id	相同id表示同一个通知
    private String		bid	;//	18	业务关联id	业务关联id
    private String		acctno	;//	15	账号	通联内部虚拟账号
    private BigDecimal amount	;//	18,2	变动金额	变动金额
    private String		trxcod	;//	10	交易类型	CP201-汇款充值上账 CP213-伞形账户上账
    private String		time	;//	25	入账时间	ISO格式[yyyy-MM-dd'T'HH:mm:ssZ]
    private String		payeraccountname	;//	120	打款方姓名	付款方账户名称
    private String		payeraccountno	;//	45	打款方银行账号	付款方账户号
    private String		payeraccountbank	;//	30	打款方银行号
    private String		payeraccountcountry	;//	3	打款方国家
    private String		ps	;//	200	附言
}
