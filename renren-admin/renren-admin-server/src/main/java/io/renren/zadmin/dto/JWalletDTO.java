package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-27
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

    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "接入密钥")
    private String accessKey;

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

    //  非数据库字段
    @Schema(description = "hkdBalance")
    private BigDecimal hkdBalance;
    @Schema(description = "usdBalance")
    private BigDecimal usdBalance;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}