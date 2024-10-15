package io.renren.zapi.exchange;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.exchange.dto.ExchangeNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiExchangeNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    // 通知商户换汇情况
    public void exchangeNotify(JExchangeEntity entity, JMerchantEntity merchant) {
        ExchangeNotify notify  = ConvertUtils.sourceToTarget(entity, ExchangeNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "exchangeNotify");
    }
}
