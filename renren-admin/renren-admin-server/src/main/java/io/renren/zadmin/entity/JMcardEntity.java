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
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_mcard")
public class JMcardEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 2 + 3
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // 4
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;
    private Integer api;

    // 申请单流水(1)
    private String meraplid;

    // 3
    private String producttype;
    private String cardtype;
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

    // 4
    private String province;
    private String city;
    private String country;
    private String address;

    // 2
    private String email;
    private String gender;

    // 2
    private String mobilecountry;
    private String mobilenumber;

    private String photofront;
    private String photoback;
    private String photofront2;
    private String photoback2;

    private String payerid;

    //  5
    private String deliverycountry;
    private String deliverypostcode;
    private String deliveryaddress;
    private String deliveryprovince;
    private String deliverycity;

    // 2
    private String payeeaccount;
    private String procurecontent;
    private String agmfid;

    // 大吉设计
    private String currency;

    // resp(1)
    private String applyid;

    // notify(4)
    private BigDecimal fee; //              decimal(18, 2) comment '申请费用',       -- fee	Number	18,2	O
    private String feecurrency; //      varchar(3) comment '申请费用币种',       -- 	feecurrency	String	3	O
    private String cardno; //           varchar(30) comment '卡号',              -- 	cardno	String	30	O	申请成功后返回
    private String state; //            varchar(2)  comment '卡申请状态',        --
    private String cardState; //            varchar(2)  comment '卡申请状态',        --
    private BigDecimal balance; //            varchar(2)  comment '卡申请状态',        --
}