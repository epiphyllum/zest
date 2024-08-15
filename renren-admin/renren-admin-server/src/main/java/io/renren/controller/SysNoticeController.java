/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.controller;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.SysNoticeDTO;
import io.renren.service.SysNoticeService;
import io.renren.service.SysNoticeUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 通知管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("notice")
@Tag(name = "通知管理")
public class SysNoticeController {
    @Resource
    private SysNoticeService sysNoticeService;
    @Resource
    private SysNoticeUserService sysNoticeUserService;
 
    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('sys:notice:all')")
    public Result<PageData<SysNoticeDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysNoticeDTO> page = sysNoticeService.page(params);

        return new Result<PageData<SysNoticeDTO>>().ok(page);
    }

    @GetMapping("user/page")
    @Operation(summary = "获取被通知的用户")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('sys:notice:all')")
    public Result<PageData<SysNoticeDTO>> userPage(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysNoticeDTO> page = sysNoticeService.getNoticeUserPage(params);

        return new Result<PageData<SysNoticeDTO>>().ok(page);
    }

    @GetMapping("mynotice/page")
    @Operation(summary = "获取我的通知")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<SysNoticeDTO>> myNoticePage(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysNoticeDTO> page = sysNoticeService.getMyNoticePage(params);

        return new Result<PageData<SysNoticeDTO>>().ok(page);
    }

    @PutMapping("mynotice/read/{noticeId}")
    @Operation(summary = "标记我的通知为已读")
    public Result read(@PathVariable("noticeId") Long noticeId) {
        sysNoticeUserService.updateReadStatus(SecurityUser.getUserId(), noticeId);

        return new Result();
    }

    @GetMapping("mynotice/unread")
    @Operation(summary = "我的通知未读数")
    public Result<Integer> unRead() {
        int count = sysNoticeUserService.getUnReadNoticeCount(SecurityUser.getUserId());

        return new Result<Integer>().ok(count);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:notice:all')")
    public Result<SysNoticeDTO> get(@PathVariable("id") Long id) {
        SysNoticeDTO data = sysNoticeService.get(id);

        return new Result<SysNoticeDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('sys:notice:all')")
    public Result save(@RequestBody SysNoticeDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysNoticeService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('sys:notice:all')")
    public Result update(@RequestBody SysNoticeDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysNoticeService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:notice:all')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysNoticeService.delete(ids);

        return new Result();
    }

}