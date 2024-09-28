package io.renren.zadmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.commons.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * j_merchant
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_sub")
public class JSubEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // (3+2) 通用
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    // (4) ID
    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;

    // 4
    private String meraplid;
    private String cusname;
    private String flag;
    private String buslicensename;

    // 4
    private String areacode;
    private String province;
    private String city;
    private String address;

    // 2
    private String cusengname;
    private String tel;

    // 6
    private String legalemail;
    private String legal;
    private String legalarea;
    private String legalidtype;
    private String legalidno;
    private String legaladdress;

    // 12
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

    // 7
    private String legalphotofrontfid;
    private String legalphotobackfid;
    private String agreementfid;
    private String creditfid;
    private String buslicensefid;
    private String taxfid;
    private String organfid;

    // (3) 审核 + enable + mcc
    private String state;
    private Integer enabled;
    private String mcc;
}
