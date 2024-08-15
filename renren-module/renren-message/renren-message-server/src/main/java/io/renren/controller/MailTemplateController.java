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
import io.renren.commons.tools.utils.JsonUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.SysMailTemplateDTO;
import io.renren.email.EmailConfig;
import io.renren.remote.ParamsRemoteService;
import io.renren.service.SysMailTemplateService;
import io.renren.utils.ModuleConstant;
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
 * 邮件模板
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("mailtemplate")
@Tag(name = "邮件模板")
public class MailTemplateController {
    @Resource
    private SysMailTemplateService sysMailTemplateService;
    @Resource
    private ParamsRemoteService paramsRemoteService;

    private final static String KEY = ModuleConstant.MAIL_CONFIG_KEY;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "name", description = "name")
    })
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result<PageData<SysMailTemplateDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysMailTemplateDTO> page = sysMailTemplateService.page(params);

        return new Result<PageData<SysMailTemplateDTO>>().ok(page);
    }

    @GetMapping("config")
    @Operation(summary = "获取配置信息")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result<EmailConfig> config() {
        EmailConfig config = paramsRemoteService.getValueObject(KEY, EmailConfig.class);

        return new Result<EmailConfig>().ok(config);
    }

    @PostMapping("saveConfig")
    @Operation(summary = "保存配置信息")
    @LogOperation("保存配置信息")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result saveConfig(@RequestBody EmailConfig config) {
        //校验数据
        ValidatorUtils.validateEntity(config);

        paramsRemoteService.updateValueByCode(KEY, JsonUtils.toJsonString(config));

        return new Result();
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result<SysMailTemplateDTO> info(@PathVariable("id") Long id) {
        SysMailTemplateDTO sysMailTemplate = sysMailTemplateService.get(id);

        return new Result<SysMailTemplateDTO>().ok(sysMailTemplate);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result save(SysMailTemplateDTO dto) {
        //校验类型
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysMailTemplateService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result update(SysMailTemplateDTO dto) {
        //校验类型
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysMailTemplateService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result delete(@RequestBody Long[] ids) {
        sysMailTemplateService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

    @PostMapping("send")
    @Operation(summary = "发送邮件")
    @LogOperation("发送邮件")
    @PreAuthorize("hasAuthority('sys:mail:all')")
    public Result send(Long id, String mailTo, String mailCc, String params) throws Exception {
        boolean flag = sysMailTemplateService.sendMail(id, mailTo, mailCc, params);
        if (flag) {
            return new Result();
        }

        return new Result().error("邮件发送失败");
    }

}