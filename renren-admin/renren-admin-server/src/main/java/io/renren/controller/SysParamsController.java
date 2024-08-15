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
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.SysParamsDTO;
import io.renren.excel.SysParamsExcel;
import io.renren.service.SysParamsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 参数管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestController
@RequestMapping("params")
@Tag(name = "参数管理")
public class SysParamsController {
    @Resource
    private SysParamsService sysParamsService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "paramCode", description = "参数编码")
    })
    @PreAuthorize("hasAuthority('sys:params:page')")
    public Result<PageData<SysParamsDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysParamsDTO> page = sysParamsService.page(params);

        return new Result<PageData<SysParamsDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:params:info')")
    public Result<SysParamsDTO> get(@PathVariable("id") Long id) {
        SysParamsDTO data = sysParamsService.get(id);

        return new Result<SysParamsDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("Save Params")
    @PreAuthorize("hasAuthority('sys:params:save')")
    public Result save(@RequestBody SysParamsDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysParamsService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("Update Params")
    @PreAuthorize("hasAuthority('sys:params:update')")
    public Result update(@RequestBody SysParamsDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysParamsService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("Delete Params")
    @PreAuthorize("hasAuthority('sys:params:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysParamsService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("Export Params")
    @PreAuthorize("hasAuthority('sys:params:export')")
    @Parameter(name = "paramCode", description = "参数编码")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysParamsDTO> list = sysParamsService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "参数管理", list, SysParamsExcel.class);
    }

    /**
     * 根据参数编码，获取参数值
     *
     * @param paramCode 参数编码
     * @return 返回参数值
     */
    @GetMapping("code/{paramCode}")
    public String getValue(@PathVariable("paramCode") String paramCode) {
        return sysParamsService.getValue(paramCode);
    }

    /**
     * 根据参数编码，更新参数值
     *
     * @param paramCode  参数编码
     * @param paramValue 参数值
     */
    @PutMapping("code/{paramCode}")
    public void updateValueByCode(@PathVariable("paramCode") String paramCode, @RequestParam("paramValue") String paramValue) {
        sysParamsService.updateValueByCode(paramCode, paramValue);
    }
}