package io.renren.zapi;

import io.renren.commons.tools.utils.Result;
import io.renren.zapi.file.ApiFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("zapi")
@Slf4j
public class ApiController {

    @Resource
    private ApiFileService apiFileService;
    @Resource
    private ApiService apiService;

    @PostMapping
    public Result<?> request(@RequestBody String body,
                             @RequestHeader("x-merchant-id") Long merchantId,
                             @RequestHeader("x-sign") String sign,
                             @RequestHeader("x-req-id") String reqId,
                             @RequestHeader("x-api-name") String name,
                             @RequestHeader(value = "x-file-suffix", required = false) String suffix
    ) {
        // 是否为文件上传
        if (suffix != null && name.equals("upload")) {
            return apiFileService.upload(merchantId, reqId, name, body, sign, suffix);
        }
        return apiService.invokeService(body, merchantId, sign, reqId, name);
    }

    // 通知我们的商户
    @PostMapping("mock/webhook")
    public String webhook(
            @RequestBody String body,
            @RequestHeader("x-merchant-id") Long merchantId,
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-req-id") String reqId,
            @RequestHeader("x-api-name") String name
    ) {
        log.info("mock webhook recv[{}][{}][{}]", merchantId, name, body);
        return "OK";
    }

}
