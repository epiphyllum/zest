package io.renren.zin.config;

import io.renren.commons.tools.exception.RenException;
import io.renren.zin.security.AESUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
@Data
@ConfigurationProperties(prefix = "zest")
public class ZestConfig {
    private AccessConfig accessConfig;
    private String publicKey;  // 平台公钥
    private String privateKey; // 平台私钥
    private Long deptId;   // 大吉的机构ID

    private boolean dev;   // 开发环境
    private boolean debug; // debug
    private String cdnUrl;  // cdn地址
    private String baseUrl;  // 接口地址
    private String uploadDir;  // 上传路径

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        System.out.println("zestConfig: " + this);
    }

    public String decryptSensitive(String sensitiveData) {
        String key = this.getAccessConfig().getSensitiveKey();
        try {
            return AESUtil.decrypt(sensitiveData,key, false, AESUtil.ECB_PKCS5, "UTF-8");
        } catch (GeneralSecurityException e) {
            throw new RenException("解密敏感数据失败");
        } catch (UnsupportedEncodingException e) {
            throw new RenException("解密敏感数据失败");
        }
    }
}