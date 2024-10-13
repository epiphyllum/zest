package io.renren.zapi.service.account.dto;

import lombok.Data;

@Data
public class MoneyAccountAdd {
    // 10
    String flag;          // 账户类型 0:个人，1：企业
    String currency;      // 币种
    String country;       // 注册国家/地区 国别信息: 账户类型为个人填写国籍账户类型为企业填写注册国家/地区
    String idtype;        // 证件类型idtypeString2C账户类型为个人时必填：注册国家/地区为中国时01-居民身份证或临时身份证注册国家/地区为香港、澳门、台湾时01-居民身份证或临时身份证04-护照其他国家/地区04-护照
    String idno;          // 证件号码idnoString30C账户类型为个人时必填
    String cardno;        // 账户号码
    String cardname;      // 账户名称
    String tel;           // 联系人联系电话t
    String email;         // 联系人邮箱
    String accountaddr;   // 联系人详细地址
    //  银行信息(7)
    String bankname;       // 银行名称
    String bankaddr;       // 开户行详情地址
    String interbankmsg;   // 中转行swiftCode
    String swiftcode;      // swiftcode
    String depositcountry; // 银行所在国家/地区
    String biccode;        //
    String branchcode;     //
}
