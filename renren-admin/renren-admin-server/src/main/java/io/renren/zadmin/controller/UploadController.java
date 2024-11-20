package io.renren.zadmin.controller;


import cn.hutool.core.date.DateUtil;
import io.renren.commons.tools.utils.Result;
import io.renren.zcommon.ZestConfig;
import io.renren.zin.file.ZinFileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("zupload")
@Slf4j
public class UploadController {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ZinFileService zinFileService;

    @PostMapping()
    public Result<String> upload(@RequestParam("file") MultipartFile file, @RequestHeader("XUpload") Boolean upload) {
        return uploadFile(file, upload);
    }

    private Result<String> uploadFile(MultipartFile file, boolean upload) {
        if (file.isEmpty()) {
            return Result.fail(9999, "非法请求");
        }
        try {
            Date now = new Date();
            String dirPart = String.format("%04d/%02d/%02d", DateUtil.year(now), DateUtil.month(now), DateUtil.dayOfMonth(now));
            String filenamePrefix = dirPart.replaceAll("/", "-") + "-";

            // 最终存储目录
            String wholeDir = zestConfig.getUploadDir() + "/" + dirPart;

            // 文件名
            String contentType = file.getContentType();
            String suffix = contentType.split("/")[1];
            String filename = filenamePrefix + UUID.randomUUID().toString().replace("-", "") + "." + suffix;

            // 创建上传目录
            Files.createDirectories(Paths.get(wholeDir));
            String filePath = wholeDir + "/" + filename;
            file.transferTo(new File(filePath));

            log.info("文件保存到:{}", filePath);

            Result<String> result = new Result<>();

            // 上传通联
            if (upload) {
                log.info("上传文件到通联: {}", filename);
                zinFileService.upload(filename);
            }

            // 文件id
            result.setData(filename);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(9999, "上传失败");
        }
    }

    private static final Map<String, String> mimeTypeMap = new HashMap<>();
    static {
        mimeTypeMap.put("txt", "text/plain");
        mimeTypeMap.put("html", "text/html");
        mimeTypeMap.put("csv", "text/csv");
        mimeTypeMap.put("json", "application/json");
        mimeTypeMap.put("xml", "application/xml");
        mimeTypeMap.put("pdf", "application/pdf");
        mimeTypeMap.put("zip", "application/zip");
        mimeTypeMap.put("jpg", "image/jpeg");
        mimeTypeMap.put("jpeg", "image/jpeg");
        mimeTypeMap.put("png", "image/png");
        mimeTypeMap.put("gif", "image/gif");
        // 其他的文件类型也可以根据需要添加
    }

    public static String getContentType(String fileName) {
        String extension = getFileExtension(fileName);
        return mimeTypeMap.getOrDefault(extension, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
    private static String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex >= 0 ? fileName.substring(dotIndex + 1).toLowerCase() : null;
    }

    @GetMapping("files/**")
    public ResponseEntity<org.springframework.core.io.Resource> files(HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        String basePath = "/sys/zupload/files/";
        String filename = fullPath.substring(basePath.length());
        Path filePath = Paths.get(zestConfig.getUploadDir(), filename);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath.toFile());
        if (!fileSystemResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            String contentType = getContentType(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileSystemResource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
