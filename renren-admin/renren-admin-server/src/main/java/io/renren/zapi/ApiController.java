package io.renren.zapi;


import cn.hutool.core.lang.Pair;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.service.accountmanage.ApiAccountManageService;
import io.renren.zapi.service.card.ApiCardService;
import io.renren.zapi.service.exchange.ApiExchangeService;
import io.renren.zapi.service.file.ApiFileService;
import io.renren.zapi.service.allocate.ApiAllocateService;
import io.renren.zapi.service.internal.ApiInternalService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("zapi")
@Slf4j
public class ApiController {

    @Resource
    private ApiAccountManageService apiAccountManageService;
    @Resource
    private ApiAllocateService apiAllocateService;
    @Resource
    private ApiCardService apiCardService;
    @Resource
    private ApiExchangeService apiExchangeService;
    @Resource
    private ApiInternalService apiInternalService;
    // 单独处理
    @Resource
    private ApiFileService apiFileService;

    @PostConstruct()
    public void init() {
        initService(apiAccountManageService);
        initService(apiCardService);
        initService(apiExchangeService);
        initService(apiAllocateService);
        initService(apiInternalService);
    }

    private Map<String, Pair<Method, Object>> methodMap = new HashMap<>();

    private void initService(Object service) {
        Method[] methods = service.getClass().getMethods();
        for (Method method : methods) {
            Class<?> returnType = method.getReturnType();
            if (returnType.isAssignableFrom(Result.class)) {
                methodMap.put(method.getName(), Pair.of(method, service));
            }
        }
    }

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

        log.info("body: {}", body);
        Pair<Method, Object> pair = methodMap.get(name);
        Method method = pair.getKey();
        Object self = pair.getValue();
        if (method == null) {
            throw new RenException("unsupported api: " + name);
        }
        try {
            Result result = (Result) method.invoke(self, merchantId, reqId, name, body, sign);
            ApiContext.getContext().info("send: {}", result);
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RenException("invocation failed");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RenException) {
                RenException ex = (RenException) cause;
                throw ex;
            } else {
                cause.printStackTrace();
                throw new RenException("unknown exception");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 通知我们的商户
    //////////////////////////////////////////////////////////////////////////////////////
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
