package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_packet
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_packet")
public class JPacketEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    private Long agentId;
    private String agentName;
    private Long merchantId;
    private String merchantName;

    private String ip;
    private String reqId;
    private String apiName;
    private String recv;
    private String send;
    private String sign;
}