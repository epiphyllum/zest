package io.renren.zin.accountmanage.dto;

import lombok.Data;

@Data
public class TBalanceDetailQuery {
    private String begdate; //开始时间	Y	ISO格式[yyyy-MM-dd'T'HH:mm:ssZ]
    private String enddate; //结束时间	Y	ISO格式[yyyy-MM-dd'T'HH:mm:ssZ]
    private String memacct; //充值账号	O	充值到账账户账号
    private String acctno;  //通联内部虚拟账号	O	通联内部虚拟账号
    private String trxid;   //系统流水号	O	可指定系统流水号查询相关明细
    private String curpag;  //页标识	O	为空时表示第1页，下一页的标识从响应获取。
}
