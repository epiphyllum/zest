/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.controller;


import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.config.DataSourceInfo;
import io.renren.dto.SysDictTypeDTO;
import io.renren.entity.MenuEntity;
import io.renren.entity.TableFieldEntity;
import io.renren.entity.TableInfoEntity;
import io.renren.feign.DictFeignClient;
import io.renren.service.GeneratorService;
import io.renren.service.TableFieldService;
import io.renren.service.TableInfoService;
import io.renren.utils.DbUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代码生成
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
public class GeneratorController {
    @Resource
    private GeneratorService generatorService;
    @Resource
    private TableInfoService tableInfoService;
    @Resource
    private TableFieldService tableFieldService;
    @Resource
    private DictFeignClient dictFeignClient;

    @GetMapping("table/page")
    public Result<PageData<TableInfoEntity>> pageTable(@RequestParam Map<String, Object> params) {
        PageData<TableInfoEntity> page = tableInfoService.page(params);

        return new Result<PageData<TableInfoEntity>>().ok(page);
    }

    @GetMapping("table/{id}")
    public Result<TableInfoEntity> getTable(@PathVariable("id") Long id) {
        TableInfoEntity table = tableInfoService.selectById(id);

        List<TableFieldEntity> fieldList = tableFieldService.getByTableName(table.getTableName());
        table.setFields(fieldList);

        return new Result<TableInfoEntity>().ok(table);
    }

    @PutMapping("table")
    public Result updateTable(@RequestBody TableInfoEntity tableInfo) {
        tableInfoService.updateById(tableInfo);

        return new Result();
    }

    @DeleteMapping("table")
    public Result deleteTable(@RequestBody Long[] ids) {
        tableInfoService.deleteBatchIds(ids);

        return new Result();
    }

    /**
     * 获取数据源中所有表
     */
    @GetMapping("datasource/table/list/{id}")
    public Result<List<TableInfoEntity>> getDataSourceTableList(@PathVariable("id") Long id) {
        try {
            //初始化配置信息
            DataSourceInfo info = generatorService.getDataSourceInfo(id);
            List<TableInfoEntity> tableInfoList = DbUtils.getTablesInfoList(info);

            return new Result<List<TableInfoEntity>>().ok(tableInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<List<TableInfoEntity>>().error("数据源配置错误，请检查数据源配置！");
        }
    }

    /**
     * 导入数据源中的表
     */
    @PostMapping("datasource/table")
    public Result datasourceTable(@RequestBody TableInfoEntity tableInfo) {
        generatorService.datasourceTable(tableInfo);

        return new Result();
    }

    /**
     * 更新列数据
     */
    @PutMapping("table/field/{tableId}")
    public Result updateTableField(@PathVariable("tableId") Long tableId, @RequestBody List<TableFieldEntity> tableFieldList) {
        generatorService.updateTableField(tableId, tableFieldList);

        return new Result();
    }

    @GetMapping("dict")
    public Result<List<SysDictTypeDTO>> dict() {
        Result<List<SysDictTypeDTO>> result = dictFeignClient.getDictTypeList();

        return result;
    }

    /**
     * 生成代码
     */
    @PostMapping("generator")
    public Result generator(@RequestBody TableInfoEntity tableInfo) {
        //保存表信息
        tableInfoService.updateById(tableInfo);

        //生成代码
        generatorService.generatorCode(tableInfo);

        return new Result<>();
    }

    /**
     * 创建菜单
     */
    @PostMapping("menu")
    public Result menu(@RequestBody MenuEntity menu) {
        //创建菜单
        generatorService.generatorMenu(menu);

        return new Result<>();
    }
}