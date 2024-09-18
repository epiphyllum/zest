package io.renren.zapi;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zin.config.CommonUtils;
import io.renren.zin.config.ZestConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class ApiService {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JMerchantDao jMerchantDao;

    @Resource
    private RestTemplate restTemplate;

    // 签名工具
    private Sign signer;

    @PostConstruct
    public void init() {
        RSA rsaSigner = new RSA(zestConfig.getPrivateKey(), null);
        this.signer = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        this.signer.setPrivateKey(rsaSigner.getPrivateKey());
    }

    public Sign getMerchantVerifier(JMerchantEntity merchant) {
        Sign verifier = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        RSA rsa = new RSA(null, merchant.getPublicKey());
        verifier.setPublicKey(rsa.getPublicKey());
        return verifier;
    }

    /**
     * 解析并验证商户请求
     * @return
     */
    public <T> T initRequest(Class<T> clazz, ApiContext context, Long merchantId, String reqId, String name, String body, String sign) {

        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
        if (merchant == null) {
            log.error("商户号错误:{}", merchantId);
            throw new RenException("invalid merchant id:" + merchantId);
        }
        context.setMerchant(merchant);
        context.setLogger(CommonUtils.getLogger("todo"));
        context.info("recv: {}|{}|{}|{}|{}", reqId, name, body, sign, merchantId);
        ApiContext.setContext(context);

        // 测试暂时不验证签名
        if (false) {
            String bodyDigest = DigestUtil.sha256Hex(body);
            String toSign = bodyDigest + reqId + merchantId;
            Sign merchantVerifier = getMerchantVerifier(merchant);
            byte[] bytes = DigestUtil.sha256(toSign);
            if (merchantVerifier.verify(bytes, sign.getBytes())) {
                throw new RenException("signature verification failed");
            }
        }

        T request = null;
        try {
            request = objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            throw new RenException("invalid json");
        }
        return request;
    }

    // 通知商户: OK| FAIL
    public String notifyMerchant(Object object, JMerchantEntity merchant, String name) {
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

