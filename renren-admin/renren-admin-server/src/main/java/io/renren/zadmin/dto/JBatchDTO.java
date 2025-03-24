package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* j_batch
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-19
*/
@Data
@Schema(description = "j_batch")
public class JBatchDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "任务名称")
    private String batchType;
    @Schema(description = "任务状态")
    private String state;
    @Schema(description = "任务日期")
    private String batchDate;
    @Schema(description = "备注")
    private String memo;
    @Schema(description = "创建者")
    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}