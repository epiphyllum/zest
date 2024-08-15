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
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.NewsDTO;
import io.renren.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 新闻
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("news")
@Tag(name = "新闻管理")
public class NewsController {
    @Resource
    private NewsService newsService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('demo:news:all')")
    public Result<PageData<NewsDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<NewsDTO> page = newsService.page(params);

        return new Result<PageData<NewsDTO>>().ok(page);
    }

    @Operation(summary = "信息")
    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('demo:news:all')")
    public Result<NewsDTO> info(@PathVariable("id") Long id) {
        NewsDTO news = newsService.get(id);

        return new Result<NewsDTO>().ok(news);
    }

    @PostMapping
    @Operation(summary = "保存")
    @PreAuthorize("hasAuthority('demo:news:all')")
    public Result save(NewsDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        newsService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @PreAuthorize("hasAuthority('demo:news:all')")
    public Result update(NewsDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        newsService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @PreAuthorize("hasAuthority('demo:news:all')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        newsService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

}