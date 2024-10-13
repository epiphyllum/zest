package io.renren.zapi.account;


import io.renren.zapi.ApiService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

// 通知商户
@Service
public class ApiAccountNotifyService {

    @Resource
    private ApiService apiService;

    public void handle() {
    }

}
