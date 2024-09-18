package io.renren.zin.service.money.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

@Data
public class TMoneyAccountAdd {
    String flag;  // 账户类型flagString1Y0:个人，1：企业
    String currency;// 币种currencyString3Y支持多币种，逗号（,）隔开
    String country;// 注册国家/地区（国籍）countryString3Y参考附录【国别信息】账户类型为个人填写国籍账户类型为企业填写注册国家/地区
    String idtype; // 证件类型idtypeString2C账户类型为个人时必填：注册国家/地区为中国时01-居民身份证或临时身份证注册国家/地区为香港、澳门、台湾时01-居民身份证或临时身份证04-护照其他国家/地区04-护照
    String idno; // 证件号码idnoString30C账户类型为个人时必填
    String cardno;// 账户号码cardnoString30Y银行卡号
    String cardname;// 账户名称cardnameString16Y
    String tel;// 联系人联系电话telString20Y
    String email;// 联系人邮箱emailString50Y
    String accountaddr;// 联系人详细地址accountaddrString100Y
    //  银行信息
    String bankname;// 银行名称banknameString16Y
    String bankaddr;// 开户行详情地址bankaddrString200Y
    String interbankmsg; // 中转行swiftCodeinterbankmsgString10O
    String swiftcode; // swiftcodeString20C银行所在国家/地区除了HKG以外，其他都必填
    String depositcountry; // 银行所在国家/地区depositcountryString3Y参考附录【国别信息】
    String biccode;
    String branchcode;
}
