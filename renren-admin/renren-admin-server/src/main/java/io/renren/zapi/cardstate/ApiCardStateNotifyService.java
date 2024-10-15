package io.renren.zapi.cardstate;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.cardstate.dto.CardChangeNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCardStateNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    public void cardChangeNotify(JCardEntity entity, JMerchantEntity merchant) {
        CardChangeNotify cardChangeNotify = ConvertUtils.sourceToTarget(entity, CardChangeNotify.class);
        apiNotifyService.notifyMerchant(cardChangeNotify, merchant, "cardChangeNotify");
    }
}
