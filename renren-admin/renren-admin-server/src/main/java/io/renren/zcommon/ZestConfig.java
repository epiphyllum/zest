package io.renren.zcommon;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.renren.commons.tools.exception.RenException;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

@Configuration
@Data
@ConfigurationProperties(prefix = "zest")
public class ZestConfig {
    private AccessConfig accessConfig; // 通联接入配置
    private String publicKey;          // 平台公钥
    private String privateKey;         // 平台私钥
    private Long deptId;               // 大吉的机构ID

    private boolean dev;               // 开发环境
    private boolean debug;             // debug
    private String cdnUrl;             // cdn地址
    private String baseUrl;            // 接口地址
    private String uploadDir;          // 上传文件路径

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        System.out.println("zestConfig: " + this);
    }


}