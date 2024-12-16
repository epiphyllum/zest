package io.renren.zadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet
 */
@Data
@Schema(description = "j_wallet")
public class JWalletDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "代理id")
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

    // 账户升级
    @Schema(description = "等级")
    private String usdLevel;
    @Schema(description = "等级")
    private String hkdLevel;
    @Schema(description = "主卡")
    private String hkdCardno;  // 港币主卡
    @Schema(description = "主卡")
    private String usdCardno;  // 美元主卡
    @Schema(description = "主卡")
    private String hkdCardid;  // 港币主卡
    @Schema(description = "主卡")
    private String usdCardid;  // 港币主卡
    //
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "接入密钥")
    private String accessKey;

    // google
    @Schema(description = "totp_key")
    private String totpKey;
    @Schema(description = "totp_status")
    private String totpStatus;

    // usdt
    @Schema(description = "usdtKey")
    private String usdtKey;
    @Schema(description = "usdtTrc20")
    private String usdtTrc20;
    @Schema(description = "usdtTrc20Ts")
    private Long usdtTrc20Ts;
    @Schema(description = "usdtTrc20Fetch")
    private Date usdtTrc20Fetch;

    // 推广
    @Schema(description = "推荐码")
    private String refcode;
    @Schema(description = "直接上级")
    private Long p1;
    @Schema(description = "间接上级")
    private Long p2;
    @Schema(description = "一级推荐数")
    private Long s1Count;
    @Schema(description = "二级推荐数")
    private Long s2Count;

    @Schema(description = "一级开卡分佣")
    private BigDecimal s1OpenFeeHkd;
    @Schema(description = "二级开卡分佣")
    private BigDecimal s2OpenFeeHkd;
    @Schema(description = "一级充值分拥")
    private BigDecimal s1ChargeFeeHkd;
    @Schema(description = "二级充值分佣")
    private BigDecimal s2ChargeFeeHkd;
    @Schema(description = "一级开卡分佣")
    private BigDecimal s1OpenFeeUsd;
    @Schema(description = "二级开卡分佣")
    private BigDecimal s2OpenFeeUsd;
    @Schema(description = "一级充值分拥")
    private BigDecimal s1ChargeFeeUsd;
    @Schema(description = "二级充值分佣")
    private BigDecimal s2ChargeFeeUsd;

    @Schema(description = "是否充值")
    private Integer charged;
    @Schema(description = "是否开卡")
    private Integer opened;
    @Schema(description = "版本号")
    private Long version;

    // 实名信息
    private String firstName;
    private String lastName;
    private String countryCode;
    private String idNo;
    private String birthday;
    private String id1FrontFid;
    private String id1BackFid;
    private String id2FrontFid;
    private String id2BackFid;
    private String realState;

    //  非数据库字段
    @Schema(description = "hkdBalance")
    private BigDecimal hkdBalance;
    @Schema(description = "usdBalance")
    private BigDecimal usdBalance;

    // 基本信息
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}