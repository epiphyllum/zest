/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.controller;

import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.service.FlowRunningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 运行中的流程
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("running")
@AllArgsConstructor
@Tag(name = "运行中的流程")
public class FlowRunningController {
    private final FlowRunningService flowRunningService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = "id", description = "实例ID"),
            @Parameter(name = "definitionKey", description = "definitionKey")
    })
    @PreAuthorize("hasAuthority('sys:running:all')")
    public Result<PageData<Map<String, Object>>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<Map<String, Object>> page = flowRunningService.page(params);

        return new Result<PageData<Map<String, Object>>>().ok(page);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除")
    @PreAuthorize("hasAuthority('sys:running:all')")
    public Result deleteInstance(@PathVariable("id") String id) {
        flowRunningService.delete(id);
        return new Result();
    }

}