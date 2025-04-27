package io.renren.zcommon;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
@ConfigurationProperties(prefix = "zest")
@EnableScheduling
public class ZestConfig {
    private AccessConfig accessConfig;    // 通联接入配置
    private AccessConfig b2bConfig;       // b2b资金
    private String publicKey;             // 平台公钥
    private String privateKey;            // 平台私钥
    private Long deptId;                  // 大吉的机构ID

    private boolean dev;                  // 开发环境
    private boolean debug;                // debug
    private String cdnUrl;                // cdn地址
    private String baseUrl;               // 接口地址
    private String uploadDir;             // 上传文件路径

    private String vccMainReal;           // 平台实体主卡
    private String vccMainVirtual;        // 平台虚拟主卡

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        System.out.println("zestConfig: " + this);
    }
}