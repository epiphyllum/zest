package io.renren.zadmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.commons.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * j_wallet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_wallet")
public class JWalletEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;

    private String hkdLevel;      // 钱包等级:  basic, premium
    private String usdLevel;      // 钱包等级:  basic, premium
    private String hkdCardno;  // 港币主卡
    private String usdCardno;  // 美元主卡
    private String hkdCardid;  // 港币主卡
    private String usdCardid;  // 美元主卡

    // 个人信息
    private String phone;      // 手机号:  不用于注册
    private String email;      // 邮箱
    private String password;   // 密码
    private String accessKey;  // 接口处理

    // google密钥
    private String totpKey;    // google
    private String totpStatus; // google

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

    // usdt
    private String usdtTrc20Key;       // 私钥
    private String usdtTrc20Address;   // 地址
    private Long usdtTrc20Ts;          // 最后一笔交易时间
    private Date usdtTrc20Fetch;       // 最近一次爬取时间

    // 推广
    private String refcode;   // 推荐码
    private Long p1;          // 上级
    private Long p2;          // 上上级
    private Long s1Count;     // 直接下级数
    private Long s2Count;     // 间接下级数

    private BigDecimal s1OpenFeeHkd;   // 开卡分佣
    private BigDecimal s2OpenFeeHkd;   // 开卡分拥
    private BigDecimal s1ChargeFeeHkd; // 充值分拥
    private BigDecimal s2ChargeFeeHkd; // 充值分拥
    private BigDecimal s1OpenFeeUsd;   // 开卡分佣
    private BigDecimal s2OpenFeeUsd;   // 开卡分拥
    private BigDecimal s1ChargeFeeUsd; // 充值分拥
    private BigDecimal s2ChargeFeeUsd; // 充值分拥

    private Integer opened;   // 是否开卡了
    private Integer charged;  // 是否充值了
    private Long version;     // 版本号

    // audit
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}