package io.renren.zapi.service.card.dto;

import lombok.Data;

// 子卡申请
@Data
public class NewCardReq {
    private String meraplid;//	申请单流水	 客户自己生成，保持唯一
    private String maincardno;//	主卡
    private String cardtype;//	卡片种类	1：虚拟卡，（主卡产品类型为：通华金服VISA虚拟卡）4：虚实同发，（主卡产品类型为：通华金服VISA公务卡、万商义乌VISA商务卡）
    private String belongtype;//	主体类型	1：员工2：合作企业
    private String cusid;//	子商户号	主体类型为合作企业时必填，【子商户创建】结果返回的cusid
    private String cardholdertype;//	持卡人身份	1：法人持有0：其他管理员
    private String nationality;//	国籍	见附录【国别信息】中的CODE,如中国：CHN
    private String companyposition;//	公司职位	1：法人代表2：董事3：高级管理员4：经理5：职员
    private String surname;//	姓氏
    private String name;//	名字
    private String birthday;//	出生日期	YYYYMMDD
    private String idtype;//	证件类型	01：居民身份证02：军人或武警身份证03：港澳台通行证04：护照05：其他有效旅行证件06：其他类个人有效证件
    private String idnumber;//	证件号码
    private String gender;//	性别	1：男0：女
    private String country;//	国家/地区	见附录【国别信息】中的CODE,如中国：CHN
    private String address;//	详情地址
    private String email;//	邮箱
    private String mobilecountry;//	手机号码所属地区	见附录【国别信息】中的PHONE_CODE
    private String mobilenumber;//	手机号码
    private String deliverypostcode;//	邮政编码
    private String photofront;//	证件正面照片	文件fid
    private String photoback;//	证件反面照片	文件fid
    private String payerid;//	申请费用扣款账户	【VA账户列表查询】响应报文中的账户唯一标识id
    private String deliverycountry;//	邮寄国家/地区	卡片类型为虚实同发时必填，见附录【国别信息】中的CODE
    private String deliveryprovince;//	邮寄省份	邮寄国家/地区为CHN时必填，见附件【地区代码】
    private String deliverycity;//	邮寄城市	邮寄国家/地区为CHN时必填，见附件【地区代码】
    private String deliveryaddress;//	邮寄城市	卡片类型为虚实同发时必填
}
