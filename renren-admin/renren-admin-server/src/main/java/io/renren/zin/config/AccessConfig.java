package io.renren.zin.config;

import lombok.Data;

// 通联接入参数
@Data
public class AccessConfig {
    private String authcus;
    private String merid;
    private String platformKey;
    private String keyId;
    private String privateKey;
    private String baseUrl;
    private String sensitiveKey;
    private String sensitiveKeyId;
}
