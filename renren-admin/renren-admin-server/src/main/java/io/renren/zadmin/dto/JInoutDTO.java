package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* j_inout
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-09-01
*/
@Data
@Schema(description = "j_inout")
public class JInoutDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "商户ID")
    private Long deptId;
    @Schema(description = "商户名称")
    private String deptName;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "交易类型")
    private String type;

    @Schema(description = "出金账户")
    private Long fromId;
    @Schema(description = "入金账户")
    private Long toId;
    @Schema(description = "出金账户")
    private Long fromName;
    @Schema(description = "入金账户")
    private Long toName;

    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}