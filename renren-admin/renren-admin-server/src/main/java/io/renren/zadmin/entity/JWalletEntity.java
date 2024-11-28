package io.renren.zadmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.commons.mybatis.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    // 个人信息
    private String phone;      // 手机号:  不用于注册
    private String email;      // 邮箱
    private String password;   // 密码
    private String totpKey;    // google
    private String totpStatus; // google
    private String usdtAddress;// usdt地址
    private String usdtKey;    // 秘钥

    private String hkdCardno;  // 港币主卡
    private String usdCardno;  // 美元主卡

    // audit
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}