/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.ScheduleJobDTO;
import io.renren.service.ScheduleJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("schedule")
@Tag(name = "定时任务")
public class ScheduleJobController {
    @Resource
    private ScheduleJobService scheduleJobService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "beanName", description = "beanName")
    })
    @PreAuthorize("hasAuthority('sys:schedule:page')")
    public Result<PageData<ScheduleJobDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<ScheduleJobDTO> page = scheduleJobService.page(params);

        return new Result<PageData<ScheduleJobDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:schedule:info')")
    public Result<ScheduleJobDTO> info(@PathVariable("id") Long id) {
        ScheduleJobDTO schedule = scheduleJobService.get(id);

        return new Result<ScheduleJobDTO>().ok(schedule);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('sys:schedule:save')")
    public Result save(@RequestBody ScheduleJobDTO dto) {
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        scheduleJobService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('sys:schedule:update')")
    public Result update(@RequestBody ScheduleJobDTO dto) {
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        scheduleJobService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:schedule:delete')")
    public Result delete(@RequestBody Long[] ids) {
        scheduleJobService.deleteBatch(ids);

        return new Result();
    }

    @PutMapping("/run")
    @Operation(summary = "立即执行")
    @LogOperation("立即执行")
    @PreAuthorize("hasAuthority('sys:schedule:run')")
    public Result run(@RequestBody Long[] ids) {
        scheduleJobService.run(ids);

        return new Result();
    }

    @PutMapping("/pause")
    @Operation(summary = "暂停")
    @LogOperation("暂停")
    @PreAuthorize("hasAuthority('sys:schedule:pause')")
    public Result pause(@RequestBody Long[] ids) {
        scheduleJobService.pause(ids);

        return new Result();
    }

    @PutMapping("/resume")
    @Operation(summary = "恢复")
    @LogOperation("恢复")
    @PreAuthorize("hasAuthority('sys:schedule:resume')")
    public Result resume(@RequestBody Long[] ids) {
        scheduleJobService.resume(ids);

        return new Result();
    }

}