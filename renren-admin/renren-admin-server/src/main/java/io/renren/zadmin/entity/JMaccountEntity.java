package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_maccount
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_maccount")
public class JMaccountEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long merchantId;
    private String merchantName;

    private String flag;
    private String currency;
    private String country;
    private String idtype;
    private String idno;
    private String cardno;
    private String cardname;
    private String tel;
    private String email;
    private String accountaddr;
    private String bankname;
    private String bankaddr;
    private String interbankmsg;
    private String swiftcode;
    private String depositcountry;
    private String biccode;
    private String branchcode;

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