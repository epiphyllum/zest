package io.renren.zin.cardapply.dto;

import lombok.Data;

// 主卡申请
@Data
public class TCardMainApplyRequest extends TCardApplyBase {
    private String producttype;        //	产品类型	001001:通华金服VISA公务卡011001:万商义乌VISA商务卡001201:通华金服VISA虚拟卡021201:通华VPA电子卡

    private String payeeaccount;       //  卡产品类型为：021201-通华VPA电子卡时必填
    private String procurecontent;     //  卡产品类型为：021201-通华VPA电子卡时必填
    private String agmfid;             //  卡产品类型为：021201-通华VPA电子卡时必填
}