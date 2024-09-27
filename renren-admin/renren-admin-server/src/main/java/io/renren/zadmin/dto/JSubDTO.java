package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_merchant
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@Schema(description = "j_merchant")
public class JSubDTO implements Serializable {
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

    // 业务字段
    @Schema(description = "跟踪号")
    private String meraplid;
    @Schema(description = "商户名称")
    private String cusname;
    @Schema(description = "商户性质")
    private String flag;
    @Schema(description = "营业执照名称")
    private String buslicensename;
    @Schema(description = "注册地")
    private String areacode;
    @Schema(description = "所在省份")
    private String province;
    @Schema(description = "所在城市")
    private String city;
    @Schema(description = "注册地址")
    private String address;
    @Schema(description = "客户英文名称")
    private String cusengname;
    @Schema(description = "联系电话")
    private String tel;
    @Schema(description = "邮箱")
    private String legalemail;
    @Schema(description = "法人姓名")
    private String legal;
    @Schema(description = "法人国籍")
    private String legalarea;
    @Schema(description = "法人证件类型")
    private String legalidtype;
    @Schema(description = "法人证件号")
    private String legalidno;
    @Schema(description = "法人代表住址")
    private String legaladdress;
    @Schema(description = "是否三证合一")
    private String threcertflag;
    @Schema(description = "营业执照代码")
    private String buslicense;
    @Schema(description = "营业执照有效期")
    private String buslicenseexpire;
    @Schema(description = "统一社会信用证代码/税务登记证代码")
    private String creditcode;
    @Schema(description = "统一社会信用证代码有效期/税务登记证代码有效期")
    private String creditcodeexpire;
    @Schema(description = "组织机构代码/公司注册证书(CR)编号")
    private String organcode;
    @Schema(description = "组织机构代码/公司注册证书(CR)编号有效期")
    private String organcodeexpire;
    @Schema(description = "法人代表职业")
    private String legaloccop;
    @Schema(description = "法人代表手机号码")
    private String legaltel;
    @Schema(description = "控股股东或实际控制人姓名")
    private String holdername;
    @Schema(description = "控股股东或实际控制人证件号")
    private String holderidno;
    @Schema(description = "控股股东或实际控制人证件有效期")
    private String holderexpire;
    @Schema(description = "法人身份证正面")
    private String legalphotofrontfid;
    @Schema(description = "法人身份证正面")
    private String legalphotobackfid;
    @Schema(description = "子卡商户合作协议")
    private String agreementfid;
    @Schema(description = "统一社会信用证及影印件")
    private String creditfid;
    @Schema(description = "营业执照")
    private String buslicensefid;
    @Schema(description = "税务登记证及影印件")
    private String taxfid;
    @Schema(description = "组织机构代码及影印件")
    private String organfid;

    // 内部审核状态
    @Schema(description = "状态")
    private String state;
    @Schema(description = "启用")
    private Integer enabled;
}