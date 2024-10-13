package io.renren.zin.accountmanage.dto;

import io.renren.zin.TResult;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TBalanceDetailResponse extends TResult {

    @Data
    public static class Item {
        private String nextpag;    //	下页标识	Y	下一页查询的页标识。为空时表示没有下页
        private String trxid;      //	系统流水号	Y
        private String memacct;    //	账号	Y	充值到账账户账号
        private String acctno;     //	通联内部虚拟账号	Y	通联内部虚拟账号
        private String memacctname;//	账户名	Y	账户名
        private String trxcode;    //	交易类型	Y
        private String direction;  //	余额方向	Y	1-借 2-贷
        private Number amount;     //	金额	Y
        private String currency;   //	币种	Y
        private Date registtime;   //	记账时间	Y
        private String remark;     //	备注	O
    }

    private List<Item> details;

}
