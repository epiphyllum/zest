package io.renren.zin;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zcommon.AccessConfig;
import io.renren.zcommon.ZestConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ZinRequester {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ObjectMapper objectMapper;
    private String commonQueryParams;
    private Sign signer;
    private Sign verifier;


    @PostConstruct
    public void init() {
        AccessConfig accessConfig = zestConfig.getAccessConfig();
        commonQueryParams = "authcus=" + accessConfig.getAuthcus() + "&merid=" + accessConfig.getMerid();

        RSA rsaSigner = new RSA(accessConfig.getPrivateKey(), null);
        this.signer = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        this.signer.setPrivateKey(rsaSigner.getPrivateKey());

        RSA rsaVerifier = new RSA(null, accessConfig.getPlatformKey());
        this.verifier = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        this.verifier.setPublicKey(rsaVerifier.getPublicKey());
    }

    private Pair<HttpHeaders, String> getHeaders(String reqId, Object body, String uri, String params) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        String agcpDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String today = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String agcpCrdt = zestConfig.getAccessConfig().getKeyId() + ":" + today + ":gcpservice";
        String agcpSend = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        StringBuilder sb = new StringBuilder();
        String toSign = null;
        if (body.getClass().isAssignableFrom(String.class)) {
            String sha256Hex = DigestUtil.sha256Hex((String) body);
            toSign = sb.append("GCP1-RSA-SHA512").append("\n")
                    .append(agcpDate).append("\n")
                    .append(uri).append("\n")
                    .append(commonQueryParams + "&reqid=" + reqId).append("\n")
                    .append(sha256Hex).toString();
        } else {
            String sha256Hex = DigestUtil.sha256Hex((byte[]) body);
            toSign = sb.append("GCP1-RSA-SHA512").append("\n")
                    .append(agcpDate).append("\n")
                    .append(uri).append("\n")
                    .append(params).append("\n")
                    .append(sha256Hex).toString();
        }

        String sign = this.signer.signHex(toSign);
        String agcpAuth = "GCP1-RSA-SHA512:" + sign;

        // prepare request headers
        HttpHeaders headers = new HttpHeaders();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) messageConverter).setDefaultCharset(Charset.forName("UTF8"));
            }
        }
        headers.add("X-AGCP-Crdt", agcpCrdt);
        headers.add("X-AGCP-Date", agcpDate);
        headers.add("X-AGCP-Send", agcpSend);
        headers.add("X-AGCP-Auth", agcpAuth);
        return Pair.of(headers, toSign);
    }

    // 传对象过来
    public <T extends TResult> T request(String reqId, String uri, Object map, Class<T> clazz) {
        String body = null;
        try {
            body = this.objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RenException("invalid request, can not convert to json");
        }
        Pair<HttpHeaders, String> headerInfo = getHeaders(reqId, body, uri, null);
        HttpHeaders headers = headerInfo.getKey();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity requestEntity = RequestEntity.post("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(headers)
                .body(body);
        String url = zestConfig.getAccessConfig().getBaseUrl() + uri + "?" + commonQueryParams + "&" + "reqid=" + reqId;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        String bodyResp = responseEntity.getBody();
        log.info("req-res: [{}][{}][{}]", url, body, bodyResp);
        try {
            T ttResult = objectMapper.readValue(bodyResp, clazz);
            if (ttResult.getRspcode().equals("0000")) {
                return ttResult;
            }
            log.error("toSign: [{}]", headerInfo.getValue());
            throw new RenException("allinpay exception:" + ttResult.getRspinfo());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RenException("request to allinpay failed");
        }
    }

    // 传map过来的
    public <T extends TResult> T request(String reqId, String uri, Map<String, Object> map, Class<T> clazz) throws JsonProcessingException {
        String body = this.objectMapper.writeValueAsString(map);
        Pair<HttpHeaders, String> headerInfo = getHeaders(reqId, body, uri, null);
        HttpHeaders headers = headerInfo.getKey();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity requestEntity = RequestEntity.post("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(headers)
                .body(body);
        String url = zestConfig.getAccessConfig().getBaseUrl() + uri + "?" + commonQueryParams + "&" + "reqid=" + reqId;
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String bodyResp = response.getBody();
        log.info("req:[{}][{}][{}]", url, body, bodyResp);
        try {
            T ttResult = objectMapper.readValue(bodyResp, clazz);
            if (!ttResult.getRspcode().equals("0000")) {
                log.error("toSign:[{}][{}]", headerInfo.getValue());
            }
            return ttResult;
        } catch (JsonProcessingException e) {
            throw new RenException("request to allinpay failed");
        }
    }

    // 上传文件
    public void upload(String reqId, byte[] bodyBytes, String fileId) {
        AccessConfig accessConfig = zestConfig.getAccessConfig();
        String params = "authcus=" + accessConfig.getAuthcus() +
                "&fid=" + fileId +
                "&merid=" + accessConfig.getMerid() +
                "&reqid=" + reqId +
                "&spos=0" +
                "&zip=0";
        String body = Base64.getUrlEncoder().encodeToString(bodyBytes);
        String uri = "/gcpapi/file/upload";
        Pair<HttpHeaders, String> headerInfo = getHeaders(reqId, bodyBytes, uri, params);
        HttpHeaders headers = headerInfo.getKey();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        RequestEntity requestEntity = RequestEntity.post("")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.ALL)
                .headers(headers)
                .body(body);

        String url = zestConfig.getAccessConfig().getBaseUrl() + uri + "?" + params;
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String bodyStr = response.getBody();
        TResult tResult = null;
        try {
            tResult = objectMapper.readValue(bodyStr, TResult.class);
            if (tResult.getRspcode().equals("0000")) {
                return;
            }
            throw new RenException("allinpay error:" + tResult.getRspinfo());
        } catch (JsonProcessingException e) {
            throw new RenException("can not upload " + fileId);
        }
    }

    public String readBody(HttpServletRequest request) {
        try {
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RenException("无法读取请求体");
        } catch (IOException e) {
            throw new RenException("无法读取请求体");
        }
    }

    // 验证通联回调
    public <T> T verify(HttpServletRequest request, String body, String auth, String date, Class<T> clazz) {
        log.info("recv notification[{}]\nbody:[{}]\nauth:[{}]\ndate:[{}]", request.getRequestURI(), body, auth, date);
        String uri = request.getRequestURI();
        String params = request.getQueryString();
        StringBuilder sb = new StringBuilder();
        String sha256Hex = DigestUtil.sha256Hex(body);
        String toSign = sb.append("GCP1-RSA-SHA512").append("\n")
                .append(date).append("\n")
                .append(uri).append("\n")
                .append(params).append("\n")
                .append(sha256Hex).toString();
        String signature = auth.split(":")[1];
        byte[] sign = HexUtil.decodeHex(signature);
        boolean verify = this.verifier.verify(toSign.getBytes(StandardCharsets.UTF_8), sign);
        if (!verify) {
            log.error("verify signature failed\ntoSign:{}", toSign);
            throw new RenException("verify failed");
        }
        try {
            return objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            log.error("can json process error:{}", e.getMessage());
            throw new RenException("can not process " + uri);
        }
    }

}
