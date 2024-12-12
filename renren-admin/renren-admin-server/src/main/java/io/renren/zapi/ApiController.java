package io.renren.zapi;

import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.file.ApiFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("zapi")
@Slf4j
public class ApiController {

    @Resource
    private ApiFileService apiFileService;
    @Resource
    private ApiService apiService;
    @Resource
    private ApiNotifyMock apiNotifyMock;

    @PostMapping
    public Result<?> request(@RequestBody String body,
                             @RequestHeader("x-merchant-id") Long merchantId,
                             @RequestHeader("x-sign") String sign,
                             @RequestHeader("x-req-id") String reqId,
                             @RequestHeader("x-api-name") String name,
                             @RequestHeader(value = "x-file-suffix", required = false) String suffix
    ) {
        // 是否为文件上传
        if (name.equals("upload")) {
            if (StringUtils.isBlank(suffix)) {
                throw new RenException("required header: x-file-suffix");
            }
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
        apiNotifyMock.handle(name);
        return "OK";
    }

}
