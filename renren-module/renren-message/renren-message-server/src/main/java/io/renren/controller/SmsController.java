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
import io.renren.commons.tools.validator.group.AliyunGroup;
import io.renren.commons.tools.validator.group.QcloudGroup;
import io.renren.commons.tools.validator.group.QiniuGroup;
import io.renren.dto.SysSmsDTO;
import io.renren.enums.PlatformEnum;
import io.renren.service.SysSmsService;
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
 * 短信服务
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("sms")
@Tag(name = "短信服务")
public class SmsController {
    @Resource
    private SysSmsService sysSmsService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('sys:sms:all')")
    public Result<PageData<SysSmsDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysSmsDTO> page = sysSmsService.page(params);

        return new Result<PageData<SysSmsDTO>>().ok(page);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('sys:sms:all')")
    public Result save(@RequestBody SysSmsDTO dto) {
        //校验数据
        if (dto.getPlatform() == PlatformEnum.ALIYUN.value()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(dto.getConfig(), AliyunGroup.class);
        } else if (dto.getPlatform() == PlatformEnum.QCLOUD.value()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(dto.getConfig(), QcloudGroup.class);
        } else if (dto.getPlatform() == PlatformEnum.QINIU.value()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(dto.getConfig(), QiniuGroup.class);
        }

        sysSmsService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('sys:sms:all')")
    public Result update(@RequestBody SysSmsDTO dto) {
        //校验数据
        if (dto.getPlatform() == PlatformEnum.ALIYUN.value()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(dto.getConfig(), AliyunGroup.class);
        } else if (dto.getPlatform() == PlatformEnum.QCLOUD.value()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(dto.getConfig(), QcloudGroup.class);
        } else if (dto.getPlatform() == PlatformEnum.QINIU.value()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(dto.getConfig(), QiniuGroup.class);
        }

        sysSmsService.update(dto);

        return new Result();
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:sms:all')")
    public Result<SysSmsDTO> info(@PathVariable("id") Long id) {
        SysSmsDTO sms = sysSmsService.get(id);

        return new Result<SysSmsDTO>().ok(sms);
    }

    @PostMapping("send")
    @Operation(summary = "发送短信")
    @LogOperation("发送短信")
    @Parameters({
            @Parameter(name = "smsCode", description = "短信编码", required = true),
            @Parameter(name = "mobile", description = "手机号码", required = true),
            @Parameter(name = "params", description = "参数", required = true),
    })
    @PreAuthorize("hasAuthority('sys:sms:all')")
    public Result send(String smsCode, String mobile, String params) {
        sysSmsService.send(smsCode, mobile, params);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:sms:all')")
    public Result delete(@RequestBody Long[] ids) {
        sysSmsService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

}