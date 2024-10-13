package io.renren.zapi;


import ch.qos.logback.classic.Logger;
import cn.hutool.core.lang.Pair;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.service.account.ApiAccountService;
import io.renren.zapi.service.allocate.ApiAllocateService;
import io.renren.zapi.service.cardapply.ApiCardApplyService;
import io.renren.zapi.service.cardmoney.ApiCardMoneyService;
import io.renren.zapi.service.cardstate.ApiCardStateService;
import io.renren.zapi.service.exchange.ApiExchangeService;
import io.renren.zapi.service.file.ApiFileService;
import io.renren.zapi.service.sub.ApiSubService;
import io.renren.zin.config.CommonUtils;
import io.renren.zin.config.ZestConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ApiService {

    @Data
    @AllArgsConstructor
    public static class ApiMeta {
        Object instance;
        Method method;
        Class<?> reqClass;
    }

    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ApiSubService apiSubService;
    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    private ApiAllocateService apiAllocateService;
    @Resource
    private ApiExchangeService apiExchangeService;
    @Resource
    private ApiCardMoneyService apiCardMoneyService;
    @Resource
    private ApiCardStateService apiCardStateService;
    @Resource
    private ApiCardApplyService apiCardApplyService;

    // 签名工具
    private Sign signer;

    public static Map<String, ApiMeta> metaMap = new HashMap<>();
    public void initService(Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            // 获取返回类型
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (returnType == Result.class && parameterTypes.length == 2 && parameterTypes[1].equals(ApiContext.class)) {
                metaMap.put(method.getName(), new ApiMeta(object, method, parameterTypes[0]));
            }
        }
    }

    /**
     *  初始化服务
     */
    @PostConstruct
    public void init() {
        RSA rsaSigner = new RSA(zestConfig.getPrivateKey(), null);
        this.signer = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        this.signer.setPrivateKey(rsaSigner.getPrivateKey());

        initService(apiSubService);
        initService(apiAccountService);
        initService(apiCardMoneyService);
        initService(apiExchangeService);
        initService(apiAllocateService);

        initService(apiCardApplyService);
        initService(apiCardMoneyService);
        initService(apiCardStateService);
    }

    public Sign getMerchantVerifier(JMerchantEntity merchant) {
        Sign verifier = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        RSA rsa = new RSA(null, merchant.getPublicKey());
        verifier.setPublicKey(rsa.getPublicKey());
        return verifier;
    }

    // 调用服务
    public Result<?> invokeService(String body, Long merchantId, String sign, String reqId, String name) {
        log.info("body: {}", body);
        ApiMeta apiMeta = metaMap.get(name);
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
        if (merchant == null) {
            throw new RenException("invalid merchant id:" + merchantId);
        }

        // 开发环境不验证签名
        if (!zestConfig.isDev()) {
            // 测试暂时不验证签名
            String bodyDigest = DigestUtil.sha256Hex(body);
            String toSign = bodyDigest + reqId + merchantId;
            Sign merchantVerifier = getMerchantVerifier(merchant);
            byte[] bytes = DigestUtil.sha256(toSign);
            if (merchantVerifier.verify(bytes, sign.getBytes())) {
                throw new RenException("signature verification failed");
            }
        }

        try {
            Object req = objectMapper.readValue(body, apiMeta.getReqClass());
            // todo: validate request
            // ValidatorUtils.validateEntity(req);

            Logger logger = CommonUtils.getLogger(merchant.getCusname());
            ApiContext context = new ApiContext(merchant, logger);
            return (Result)apiMeta.getMethod().invoke(apiMeta.getInstance(), req, context);
        } catch (JsonProcessingException e) {
            throw new RenException("invalid json");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RenException) {
                RenException ex = (RenException) cause;
                throw ex;
            } else {
                cause.printStackTrace();
                throw new RenException("unknown exception");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // 通知商户: OK| FAIL
    public String notifyMerchant(Object object, JMerchantEntity merchant, String apiName) {
        String body = null;
        try {
            body = this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RenException("invalid request, can not convert to json");
        }

        // 设置基本请求头
        HttpHeaders headers = new HttpHeaders();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) messageConverter).setDefaultCharset(Charset.forName("UTF8"));
            }
        }

        String reqId = CommonUtils.newRequestId();
        String bodyDigest = DigestUtil.sha256Hex(body);
        String toSign = bodyDigest + reqId + merchant.getId().toString();
        String sign = this.signer.signHex(toSign);

        headers.add("x-api-name", apiName);
        headers.add("x-merchant-id", merchant.getId().toString());
        headers.add("x-req-id", CommonUtils.newRequestId());
        headers.add("x-sign", sign);

        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity requestEntity = RequestEntity.post("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(headers)
                .body(body);

        // 商户webhook地址
        String url = merchant.getWebhook();
        log.info("req:[{}][{}]", url, body);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        String result = responseEntity.getBody();
        log.info("res:[{}]", result);
        return result;
    }
}

