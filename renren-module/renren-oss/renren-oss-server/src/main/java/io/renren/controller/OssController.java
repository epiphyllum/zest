/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import cn.hutool.core.map.MapUtil;
import io.renren.cloud.CloudStorageConfig;
import io.renren.cloud.OssFactory;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.JsonUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AliyunGroup;
import io.renren.commons.tools.validator.group.QcloudGroup;
import io.renren.commons.tools.validator.group.QiniuGroup;
import io.renren.dto.UploadDTO;
import io.renren.entity.OssEntity;
import io.renren.enums.OssTypeEnum;
import io.renren.exception.ModuleErrorCode;
import io.renren.remote.ParamsRemoteService;
import io.renren.service.OssService;
import io.renren.utils.ModuleConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("file")
@Tag(name = "文件上传")
public class OssController {
    @Resource
    private OssService ossService;
    @Resource
    private ParamsRemoteService paramsRemoteService;

    private final static String KEY = ModuleConstant.CLOUD_STORAGE_CONFIG_KEY;

    @GetMapping("page")
    @Operation(summary = "分页")
    @PreAuthorize("hasAuthority('sys:oss:all')")
    public Result<PageData<OssEntity>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<OssEntity> page = ossService.page(params);

        return new Result<PageData<OssEntity>>().ok(page);
    }

    @GetMapping("info")
    @Operation(summary = "云存储配置信息")
    @PreAuthorize("hasAuthority('sys:oss:all')")
    public Result<CloudStorageConfig> info() {
        CloudStorageConfig config = paramsRemoteService.getValueObject(KEY, CloudStorageConfig.class);

        return new Result<CloudStorageConfig>().ok(config);
    }

    @PostMapping
    @Operation(summary = "保存云存储配置信息")
    @LogOperation("保存云存储配置信息")
    @PreAuthorize("hasAuthority('sys:oss:all')")
    public Result saveConfig(@RequestBody CloudStorageConfig config) {
        //校验类型
        ValidatorUtils.validateEntity(config);

        if (config.getType() == OssTypeEnum.QINIU.value()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(config, QiniuGroup.class);
        } else if (config.getType() == OssTypeEnum.ALIYUN.value()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(config, AliyunGroup.class);
        } else if (config.getType() == OssTypeEnum.QCLOUD.value()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(config, QcloudGroup.class);
        }

        paramsRemoteService.updateValueByCode(KEY, JsonUtils.toJsonString(config));

        return new Result();
    }

    @PostMapping("upload")
    @Operation(summary = "上传文件")
    public Result<UploadDTO> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return new Result<UploadDTO>().error(ModuleErrorCode.UPLOAD_FILE_EMPTY);
        }

        //上传文件
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String url = OssFactory.build().uploadSuffix(file.getBytes(), extension);

        //保存文件信息
        OssEntity ossEntity = new OssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        ossService.insert(ossEntity);

        //文件信息
        UploadDTO dto = new UploadDTO();
        dto.setUrl(url);
        dto.setSize(file.getSize());

        return new Result<UploadDTO>().ok(dto);
    }

    @PostMapping("tinymce/upload")
    @Operation(summary = "TinyMCE上传文件")
    public Map<String, String> tinymceUpload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return MapUtil.newHashMap();
        }

        //上传文件
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String url = OssFactory.build().uploadSuffix(file.getBytes(), extension);

        //保存文件信息
        OssEntity ossEntity = new OssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        ossService.insert(ossEntity);

        Map<String, String> data = new HashMap<>(1);
        data.put("location", url);

        return data;
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('sys:oss:all')")
    public Result delete(@RequestBody Long[] ids) {
        ossService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

}