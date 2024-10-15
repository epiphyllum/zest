package io.renren.zapi.account;


import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.account.dto.MoneyAccountNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

// 通知商户
@Service
public class ApiAccountNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    public void moneyAccountNotify(JMaccountEntity entity, JMerchantEntity merchant) {
        MoneyAccountNotify notify = ConvertUtils.sourceToTarget(entity, MoneyAccountNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "moneyAccountNotify");
    }
}
