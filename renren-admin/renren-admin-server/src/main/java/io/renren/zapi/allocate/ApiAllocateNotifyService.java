package io.renren.zapi.allocate;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.ApiService;
import io.renren.zapi.allocate.dto.MoneyNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAllocateNotifyService {

    @Resource
    private ApiNotifyService apiNotifyService;

    // 入金通知
    public void moneyNotify(JMoneyEntity entity, JMerchantEntity merchant) {
        MoneyNotify notify = ConvertUtils.sourceToTarget(entity, MoneyNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "moneyNotify");
    }


}
