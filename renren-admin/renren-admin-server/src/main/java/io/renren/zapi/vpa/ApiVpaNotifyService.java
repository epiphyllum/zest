package io.renren.zapi.vpa;

import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.vpa.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiVpaNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    public void vpaJobNotify(JVpaJobEntity entity, JMerchantEntity merchant) {
        VpaJobNotify jobNotify = new VpaJobNotify();
        apiNotifyService.notifyMerchant(jobNotify, merchant, "vpaJobNotify");
    }
}
