/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.renren.commons.tools.utils.DateUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Data
@Schema(description = "定时任务")
public class ScheduleJobDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @Null(message = "{id.null}", groups = AddGroup.class)
    @NotNull(message = "{id.require}", groups = UpdateGroup.class)
    private Long id;

    @Schema(description = "spring bean名称")
    @NotBlank(message = "{schedule.bean.require}", groups = DefaultGroup.class)
    private String beanName;

    @Schema(description = "参数")
    private String params;

    @Schema(description = "cron表达式")
    @NotBlank(message = "{schedule.cron.require}", groups = DefaultGroup.class)
    private String cronExpression;

    @Schema(description = "任务状态  0：暂停  1：正常")
    @Range(min = 0, max = 1, message = "{schedule.status.range}", groups = DefaultGroup.class)
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date createDate;

}
