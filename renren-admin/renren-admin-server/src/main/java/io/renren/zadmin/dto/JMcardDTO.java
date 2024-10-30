package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@Schema(description = "j_card")
public class JMcardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

    // ID相关
    @Schema(description = "代理ID")
    private Long agentId;
    @Schema(description = "代理")
    private String agentName;

    @Schema(description = "商户ID")
    private Long merchantId;
    @Schema(description = "商户")
    private String merchantName;
    @Schema(description = "子商户ID")
    private Long subId;
    @Schema(description = "子商户")
    private String subName;

    private Integer api;

    // 业务字段
    @Schema(description = "申请单流水")
    private String meraplid;

    //
    @Schema(description = "产品类型")
    private String producttype;
    @Schema(description = "卡片种类")
    private String cardtype;
    @Schema(description = "持卡人身份")
    private String cardholdertype;

    @Schema(description = "通联子商户号")
    private String cusid;

    @Schema(description = "国籍")
    private String nationality;
    @Schema(description = "公司职位")
    private String companyposition;
    @Schema(description = "姓氏")
    private String surname;
    @Schema(description = "名字")
    private String name;
    @Schema(description = "出生日期")
    private String birthday;
    @Schema(description = "证件1类型")
    private String idtype;
    @Schema(description = "证件1号码")
    private String idnumber;

    @Schema(description = "居住国家/地区")
    private String country;
    @Schema(description = "详细地址")
    private String address;
    @Schema(description = "省份")
    private String province;
    @Schema(description = "城市")
    private String city;


    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "手机号码所属地区")
    private String mobilecountry;
    @Schema(description = "手机号码")
    private String mobilenumber;

    @Schema(description = "正面照片")
    private String photofront;
    @Schema(description = "反面照片")
    private String photoback;
    @Schema(description = "正面照片2")
    private String photofront2;
    @Schema(description = "反面照片2")
    private String photoback2;

    @Schema(description = "申请费用扣款账户")
    private String payerid;

    @Schema(description = "邮寄国家/地区")
    private String deliverycountry;
    @Schema(description = "邮政编码")
    private String deliverypostcode;
    @Schema(description = "邮寄地址")
    private String deliveryaddress;
    @Schema(description = "邮寄省份")
    private String deliveryprovince;
    @Schema(description = "邮寄城市")
    private String deliverycity;

    @Schema(description = "交易对手")
    private String payeeaccount;
    @Schema(description = "采购内容")
    private String procurecontent;
    @Schema(description = "保证金协议")
    private String agmfid;

    // 大吉设计
    @Schema(description = "币种ID")
    private String currency;
    @Schema(description = "balance")
    private BigDecimal balance;

    // 通联返回
    @Schema(description = "申请ID")
    private String applyid;

    // 结果
    @Schema(description = "fee")
    private BigDecimal fee; //              decimal(18, 2) comment '申请费用',       -- fee	Number	18,2	O
    @Schema(description = "feecurrency")
    private String feecurrency; //      varchar(3) comment '申请费用币种',       -- 	feecurrency	String	3	O
    @Schema(description = "cardno")
    private String cardno; //           varchar(30) comment '卡号',              -- 	cardno	String	30	O	申请成功后返回
    @Schema(description = "state")
    private String state; //            varchar(2)  comment '卡申请状态',        --
    @Schema(description = "cardState")
    private String cardState; //            varchar(2)  comment '卡申请状态',        --

    @Schema(description = "cvv")
    private String cvv;
    @Schema(description = "expiredate")
    private String expiredate;
}