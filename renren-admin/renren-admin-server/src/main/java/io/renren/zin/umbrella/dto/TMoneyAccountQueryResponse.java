package io.renren.zin.umbrella.dto;

import io.renren.zin.TResult;
import lombok.Data;

@Data
public class TMoneyAccountQueryResponse extends TResult {
    // 个人信息(10)
    String flag;          // 账户类型 0:个人，1：企业
    String currency;      // 币种, 支持多币种，逗号（,）隔开
    String country;       // 注册国家/地区（国籍） 参考附录【国别信息】账户类型为个人填写国籍  账户类型为企业填写注册国家/地区
    String idtype;        // 证件类型
    String idno;          // 证件号码 账户类型为个人时必填
    String cardno;        // 账户号码
    String cardname;      // 账户名称
    String tel;           // 联系人联系电话
    String email;         // 联系人邮箱
    String accountaddr;   // 联系人详细地址
    //  银行信息(7)
    String bankname;       // 银行名称
    String bankaddr;       // 开户行详情地址
    String interbankmsg;   // 中转行
    String swiftcode;      // swiftcode, 银行所在国家/地区除了HKG以外，其他都必填
    String depositcountry; // 银行所在国家/地区depositcountryString3Y参考附录【国别信息】
    String biccode;
    String branchcode;


    String id;
    String state;
    String stateexplain;
}
