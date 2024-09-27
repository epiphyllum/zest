package io.renren.zin.service.umbrella.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

// 1001 - VA入金信息查询
@Data
public class TVaAccountDetailResponse extends TResult {
    private String accountname;  //	账户名称
    private String swiftcode;    //	SWIFT代码
    private String cusaddr;      //	客户地址
    private String bankname;     //	银行名称
    private String bankaddr;     //	银行地址
    private String vaaccountno;  //	银行账号
}