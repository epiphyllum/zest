package io.renren.zapi.sub;

import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.sub.dto.SubNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiSubNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    public void subNotify(JSubEntity entity, JMerchantEntity merchant) {
        SubNotify notify = new SubNotify(entity.getId(), entity.getCusname(), entity.getState());
        apiNotifyService.notifyMerchant(notify, merchant, "subNotify");
    }
}
