package io.renren.zadmin.entity;

import io.renren.zcommon.AccessConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_merchant
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_merchant")
public class JMerchantEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // id(2)
    private Long agentId;
    private String agentName;

    // (4)
    private String meraplid;
    private String cusname;
    private String flag;
    private String buslicensename;

    // (4)
    private String areacode;
    private String province;
    private String city;
    private String address;

    // (2)
    private String cusengname;
    private String tel;

    // (6)
    private String legalemail;
    private String legal;
    private String legalarea;
    private String legalidtype;
    private String legalidexpire;
    private String legalidno;
    private String legaladdress;

    // (12)
    private String threcertflag;
    private String buslicense;
    private String buslicenseexpire;
    private String creditcode;
    private String creditcodeexpire;
    private String organcode;
    private String organcodeexpire;
    private String legaloccop;
    private String legaltel;
    private String holdername;
    private String holderidno;
    private String holderexpire;

    // (7)
    private String legalphotofrontfid;
    private String legalphotobackfid;
    private String agreementfid;
    private String credifid;
    private String buslicensefid;
    private String organfid;

    // (2) 通联返回
    private String cusid;
    private String state;

    // (1)管理
    private Integer enabled;
    private Integer debug;
    private String b2bva;


    // (7)商户接入参数, 只有商户有， 子商户没有
    private String mcc;
    private String currencyList;

    private String publicKey;
    private String sensitiveKey;
    private String webhook;
    private String whiteIp;

    // 开卡 充值 参数
    private String vpaCardFid;
    private String vpaChargeFid;
    private String vpaPayeeaccount;
    private String vpaProcurecontent;

    /////////////
    @TableField(exist = false)
    private AccessConfig b2bConfig;
}