package io.renren.zcommon;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.util.List;
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