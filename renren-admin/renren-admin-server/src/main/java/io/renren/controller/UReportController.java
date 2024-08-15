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
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.dto.UReportDataDTO;
import io.renren.service.UReportDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 报表管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@AllArgsConstructor
@RestController
@RequestMapping("ureport2")
@Tag(name = "报表管理")
public class UReportController {
    private final UReportDataService ureportDataService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "fileName", description = "文件名")
    })
    @PreAuthorize("hasAuthority('sys:ureport:all')")
    public Result<PageData<UReportDataDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<UReportDataDTO> page = ureportDataService.page(params);

        return new Result<PageData<UReportDataDTO>>().ok(page);
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:ureport:all')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        ureportDataService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

}