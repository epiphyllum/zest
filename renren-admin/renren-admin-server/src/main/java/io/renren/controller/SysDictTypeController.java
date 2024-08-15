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
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.SysDictTypeDTO;
import io.renren.entity.DictType;
import io.renren.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 字典类型
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("dict/type")
@Tag(name = "字典类型")
public class SysDictTypeController {
    @Resource
    private SysDictTypeService sysDictTypeService;

    @GetMapping("page")
    @Operation(summary = "字典类型")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "dictType", description = "字典类型"),
            @Parameter(name = "dictName", description = "字典名称")
    })
    @PreAuthorize("hasAuthority('sys:dict:page')")
    public Result<PageData<SysDictTypeDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        //字典类型
        PageData<SysDictTypeDTO> page = sysDictTypeService.page(params);

        return new Result<PageData<SysDictTypeDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:dict:info')")
    public Result<SysDictTypeDTO> get(@PathVariable("id") Long id) {
        SysDictTypeDTO data = sysDictTypeService.get(id);

        return new Result<SysDictTypeDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('sys:dict:save')")
    public Result save(@RequestBody SysDictTypeDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, DefaultGroup.class);

        sysDictTypeService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('sys:dict:update')")
    public Result update(@RequestBody SysDictTypeDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysDictTypeService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:dict:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysDictTypeService.delete(ids);

        return new Result();
    }

    @GetMapping("all")
    @Operation(summary = "所有字典数据")
    public Result<List<DictType>> all() {
        List<DictType> list = sysDictTypeService.getAllList();

        return new Result<List<DictType>>().ok(list);
    }

    @GetMapping("list")
    public Result<List<SysDictTypeDTO>> list() {
        List<DictType> list = sysDictTypeService.getDictTypeList();
        List<SysDictTypeDTO> typeList = ConvertUtils.sourceToTarget(list, SysDictTypeDTO.class);
        return new Result<List<SysDictTypeDTO>>().ok(typeList);
    }

}