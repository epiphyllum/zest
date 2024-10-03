package io.renren.zin.config;

import io.renren.commons.tools.exception.RenException;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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

    private List<CardProductConfig> cardProductConfigs;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        System.out.println("zestConfig: " + this);
    }

    public CardProductConfig getCardProductConfig(String producttype, String currency, String cardtype) {
        // 查询开卡费用配置
        CardProductConfig config = null;
        for (CardProductConfig cardProductConfig : cardProductConfigs) {
            System.out.println("try: " + cardProductConfig);
            if (producttype.equals(cardProductConfig.getProducttype()) &&
                    currency.equals(cardProductConfig.getCurrency()) &&
                    cardtype.equals(cardProductConfig.getCardtype())
            ) {
                config = cardProductConfig;
                break;
            }
        }
        if (config == null) {
            throw new RenException("无法找到卡产品费用配置:" + producttype + "|" + currency + "|" + cardtype);
        }
        return config;
    }
}