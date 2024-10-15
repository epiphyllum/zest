package io.renren.zadmin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import io.renren.commons.mybatis.entity.BaseEntity;

/**
 * j_channel_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("j_channel_log")
public class JChannelLogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private String apiName;
    private String recv;
    private String send;
    private String sign;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

}