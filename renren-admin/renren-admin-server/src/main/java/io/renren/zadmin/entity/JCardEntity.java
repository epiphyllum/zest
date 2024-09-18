package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_card")
public class JCardEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     *  可能是在子商户， 也有
     */
    @TableField(fill = FieldFill.INSERT)
    private Long deptId;
    private String deptName;

    //
    private Long merchantId;
    private String merchantName;

    /**
     * 申请单流水
     */
    private String meraplid;
    private String maincardno;
    private String cusid;
    private String producttype;
    private String cardtype;
    private String belongtype;
    private String cardholdertype;
    private String nationality;
    private String companyposition;
    private String surname;
    private String name;
    private String birthday;
    private String idtype;
    private String idnumber;
    private String idtype2;
    private String idnumber2;
    private String province;
    private String city;
    private String country;
    private String address;
    private String email;
    private String gender;
    private String mobilecountry;
    private String mobilenumber;
    private String photofront;
    private String photoback;
    private String payerid;
    private String deliverycountry;
    private String deliveryprovince;
    private String deliverycity;
    private String deliveryaddress;
    // resp
    private String applyid;
    // notify
    private BigDecimal fee; //              decimal(18, 2) comment '申请费用',       -- fee	Number	18,2	O
    private String feecurrency; //      varchar(3) comment '申请费用币种',       -- 	feecurrency	String	3	O
    private String cardno; //           varchar(30) comment '卡号',              -- 	cardno	String	30	O	申请成功后返回
    private String state; //            varchar(2)  comment '卡申请状态',        --

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}