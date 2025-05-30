package io.renren.zcommon;

import lombok.Data;
import java.util.List;

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
    private String b2bVa;
}
