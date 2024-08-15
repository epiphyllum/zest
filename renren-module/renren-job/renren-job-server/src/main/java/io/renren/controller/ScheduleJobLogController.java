/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.dto.ScheduleJobLogDTO;
import io.renren.service.ScheduleJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("scheduleLog")
@Tag(name = "定时任务日志")
public class ScheduleJobLogController {
    @Resource
    private ScheduleJobLogService scheduleJobLogService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "jobId", description = "jobId")
    })
    @PreAuthorize("hasAuthority('sys:schedule:log')")
    public Result<PageData<ScheduleJobLogDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<ScheduleJobLogDTO> page = scheduleJobLogService.page(params);

        return new Result<PageData<ScheduleJobLogDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:schedule:log')")
    public Result<ScheduleJobLogDTO> info(@PathVariable("id") Long id) {
        ScheduleJobLogDTO log = scheduleJobLogService.get(id);

        return new Result<ScheduleJobLogDTO>().ok(log);
    }
}