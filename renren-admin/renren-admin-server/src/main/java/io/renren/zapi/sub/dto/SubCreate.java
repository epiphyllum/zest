package io.renren.zapi.sub.dto;

import lombok.Data;

// 创建子商户
@Data
public class SubCreate {
    private String meraplid;           //	跟踪号	32	Y	 客户自己生成，保持唯一

    private String cusname;            //	商户名称	30	Y
    private String flag;               //	商户性质	3	Y	1: 合资、股份制、民营2: 世界500强或国有3:个体户4: 个人
    private String buslicensename;     //	营业执照名称	100	Y

    private String areacode;           //	注册国家/地区	10	Y	见附录【国别信息】中的CODE,如中国：CHN
    private String province;           //	所在省份	10	C	注册国家为中国时必填，见附件【地区代码】
    private String city;               //	所在城市	10	C	注册国家为中国时必填，见附件【地区代码】
    private String address;            //	注册（联系）地址	100	Y

    private String cusengname;         //	客户英文名称	100	Y
    private String tel;                //	联系电话	30	Y

    private String legalemail;         //	邮箱	30	Y
    private String legal;              //	法人姓名	30	Y
    private String legalarea;          //	法人国籍	10	Y	见附录【国别信息】中的CODE,如中国：CHN
    private String legalidtype;        //	法人证件类型	10	Y	01：居民身份证02：军人或武警身份证03：港澳台通行证04：护照05：其他有效旅行证件06：其他类个人有效证件
    private String legalidno;          //	法人证件号	30	Y
    private String legalidexpire;      //	法人证件有效期	30	Y	YYYYMMDD
    private String legaladdress;       //	法人代表住址	200	Y

    private String threcertflag;       //	是否三证合一	2	C	0-否、1-是 注册国家/地区areacode为CHN时必填
    private String buslicense;         //	营业执照代码	10	C	注册国家/地区areacode不为CHN时，或者注册国家/地区areacode为CHN，三证不合一时，填营业执照代码
    private String buslicenseexpire;   //	营业执照有效期	30	C	YYYYMMDD，注册国家/地区areacode不为CHN时，或者注册国家/地区areacode为CHN，三证不合一时，填营业执照有效期
    private String creditcode;         //	统一社会信用证代码/税务登记证代码	30	C	注册国家/地区areacode为CHN，三证合一时，填入统一社会信用证代码；三证不合一时，填入税务登记证代码有效期
    private String creditcodeexpire;   //	统一社会信用证代码有效期/税务登记证代码有效期	30	C	YYYYMMDD，注册国家/地区areacode为CHN，三证合一时，填入统一社会信用证代码有效期；三证不合一时，填入税务登记证代码有效期
    private String organcode;          //	组织机构代码/公司注册证书(CR)编号	30	C	注册国家/地区areacode为CHN，三证不合一时，填入组织机构代码
    private String organcodeexpire;    //	组织机构代码/公司注册证书(CR)编号有效期	30	C	注册国家/地区areacode为CHN，三证不合一时，填入组织机构代码有效期
    private String legaloccop;         //	法人代表职业	30	O
    private String legaltel;           //	法人代表手机号码	30	O

    private String holdername;         //	控股股东或实际控制人姓名	30	O
    private String holderidno;         //	控股股东或实际控制人证件号	30	O
    private String holderexpire;       //	控股股东或实际控制人证件有效期	30	O	YYYYMMDD

    private String legalphotofrontfid; //	法人身份证正面	200	Y	附件fid
    private String legalphotobackfid;  //	法人身份证正面	200	Y	附件fid

    private String agreementfid;       //	子卡商户合作协议	200	Y	附件fid
    private String credifid;           //	统一社会信用证及影印件	200	C	注册国家/地区areacode为CHN，三证合一时，填入统一社会信用证及影印件上传文件fid
    private String buslicensefid;      //	营业执照	200	C	附件fid
    private String organfid;           //	组织机构代码及影印件	200	C	注册国家/地区areacode为CHN，三证不合一时，填入组织机构代码及影印件上传文件fid
}
