package io.renren.zadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_card:  子卡
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_card")
public class JCardEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 3 + 2
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // ID(6)
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Long subId;
    private String subName;
    private Integer api;

    //
    private String marketproduct;
    private String maincardno;
    private Long walletId;
    private String walletName;

    // 请求数据(5)
    private String meraplid;
    private String cusid;
    private String producttype;
    private String cardtype;
    private String belongtype;
    private String cardholdertype;

    // 2
    private String nationality;
    private String companyposition;

    // 3
    private String surname;
    private String name;
    private String birthday;

    // 4
    private String idtype;
    private String idnumber;
    private String idtype2;
    private String idnumber2;

    // 6
    private String province;
    private String city;
    private String country;
    private String address;
    private String email;
    private String gender;

    // 3
    private String mobilecountry;
    private String mobilenumber;
    private String deliverypostcode;

    // 4
    private String photofront;
    private String photoback;
    private String photofront2;
    private String photoback2;

    // 1
    private String payerid;

    // 4
    private String deliverycountry;
    private String deliveryprovince;
    private String deliverycity;
    private String deliveryaddress;

    // 共享卡要求字段
    private String payeeaccount;
    private String procurecontent;
    private String agmfid;

    // 大吉设计
    private String currency;
    private BigDecimal merchantfee;
    private Integer notifyStatus;
    private Integer notifyCount;

    // resp
    private String applyid;
    private String txnid;

    // notify
    private BigDecimal fee;       // 申请费用
    private String feecurrency;   // 申请费用币种
    private String cardno;        // 卡号
    private String cardstate;     //            varchar(2)  comment '卡申请状态',        --
    private String state;         // 卡申请状态
    private String stateexplain;  // 卡申请状态
    private BigDecimal balance;   //            varchar(2)  comment '卡申请状态',        --

    // cvv + expire date
    private String cvv;
    private String expiredate;

    // 共享卡相关信息
    private String sceneid;
    private String scenename;
    private String cycle;
    private Integer authmaxcount;
    private BigDecimal authmaxamount;
    private String onlhkflag;
    private String begindate;
    private String enddate;
    private String naturalmonthflag;
    private String naturalmonthstartday;
    private String fixedamountflag;
    private String permitCurrency;
    private Long vpaJob;  // 对应到哪个job
}