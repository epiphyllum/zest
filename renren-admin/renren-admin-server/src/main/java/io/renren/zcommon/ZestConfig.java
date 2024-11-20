package io.renren.zcommon;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
@Data
@ConfigurationProperties(prefix = "zest")
public class ZestConfig {
    private AccessConfig accessConfig;    // 通联接入配置
    private String publicKey;             // 平台公钥
    private String privateKey;            // 平台私钥
    private Long deptId;                  // 大吉的机构ID

    private boolean dev;                  // 开发环境
    private boolean debug;                // debug
    private String cdnUrl;                // cdn地址
    private String baseUrl;               // 接口地址
    private String uploadDir;             // 上传文件路径
    private Map<String, String> mainMap;  // vcc产品类型-> 主卡卡号

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        System.out.println("zestConfig: " + this);
    }
}