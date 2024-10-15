package io.renren.zapi.cardapply;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.cardapply.dto.CardNewNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCardApplyNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    /**
     * 发卡状态通知
     * @param entity
     * @param merchant
     */
    public void cardNewNotify(JCardEntity entity, JMerchantEntity merchant) {
        CardNewNotify notify = ConvertUtils.sourceToTarget(entity, CardNewNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "cardNewNotify");
    }
}
