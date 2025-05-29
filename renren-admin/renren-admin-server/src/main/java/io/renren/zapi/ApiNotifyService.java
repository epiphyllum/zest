package io.renren.zapi;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JPacketEntity;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
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
public class ApiNotifyService {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ApiLogger apiLogger;

    // 签名工具
    private Sign signer;

    @PostConstruct
    public void init() {
        RSA rsaSigner = new RSA(zestConfig.getPrivateKey(), null);
        this.signer = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        this.signer.setPrivateKey(rsaSigner.getPrivateKey());
    }

    // 通知商户: OK| FAIL
    public String notifyMerchant(Object object, JMerchantEntity merchant, String apiName) {
        String body = null;
        try {
            body = this.objectMapper.writeValueAsString(object);
            log.info("通知商户: api:{}, body:{}", apiName, body);
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

        String reqId = CommonUtils.uniqueId();
        String bodyDigest = DigestUtil.sha256Hex(body);
        String toSign = bodyDigest + reqId + merchant.getId().toString() + apiName;
        byte[] bytes = DigestUtil.sha256(toSign);
        String sign = this.signer.signHex(bytes);

        headers.add("x-api-name", apiName);
        headers.add("x-merchant-id", merchant.getId().toString());
        headers.add("x-req-id", reqId);
        headers.add("x-sign", sign);

        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity requestEntity = RequestEntity.post("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(headers)
                .body(body);

        // 记录日志准备
        JPacketEntity packetEntity = new JPacketEntity();
        packetEntity.setMerchantId(merchant.getId());
        packetEntity.setMerchantName(merchant.getCusname());
        packetEntity.setAgentId(merchant.getAgentId());
        packetEntity.setAgentName(merchant.getAgentName());
        packetEntity.setSend(body.length() > 2047 ? body.substring(0,2047) : body);
        packetEntity.setApiName(apiName);
        packetEntity.setSign(sign);
        packetEntity.setReqId(reqId);

        // 商户webhook地址
        String url = merchant.getWebhook();
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            String result = responseEntity.getBody();
            if (result == null) {
                result = "No Response";
            }
            packetEntity.setRecv(result);
            apiLogger.logPacketSuccess(packetEntity);
            return result;
        } catch (Exception ex) {
            apiLogger.logPacketException(packetEntity, ex);
            throw  ex;
        }
    }
}
