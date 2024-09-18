package io.renren.zapi;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.renren.zapi.dto.DepositRequest;
import io.renren.zapi.dto.DepositResponse;
import io.renren.zapi.dto.Result;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("zdemo")
@Slf4j
public class ZClientController {
    @Value("zclient.webhookUrl")
    private String webhookUrl;
    @Value("zclient.platformKey")
    private String platformKey;
    @Value("zclient.privateKey")
    private String privateKey;
    @Value("zclient.baseUrl")
    private String baseUrl;

    private ApiClient apiClient;

    @PostConstruct
    public void init() {
        this.apiClient = new ApiClient(baseUrl, webhookUrl, privateKey, platformKey);
    }

    @PostMapping("deposit")
    public Result<DepositResponse> charge(@RequestBody DepositRequest request) throws JsonProcessingException {
        return null;
    }
}
