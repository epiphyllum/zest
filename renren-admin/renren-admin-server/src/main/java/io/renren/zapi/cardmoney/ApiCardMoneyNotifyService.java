package io.renren.zapi.cardmoney;

import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.cardmoney.dto.CardChargeNotify;
import io.renren.zapi.cardmoney.dto.CardTxnNotify;
import io.renren.zapi.cardmoney.dto.CardWithdrawNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCardMoneyNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    /**
     * 卡交易通知接口
     * @param entity
     * @param merchant
     */
    public void cardTxnNotify(JAuthEntity entity, JMerchantEntity merchant) {
        CardTxnNotify notify = ConvertUtils.sourceToTarget(entity, CardTxnNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "cardTxnNotify");
    }

    /**
     * 卡提现通知
     * @param entity
     * @param merchant
     */
    public void cardWithdrawNotify(JWithdrawEntity entity, JMerchantEntity merchant) {
        CardWithdrawNotify notify = ConvertUtils.sourceToTarget(entity, CardWithdrawNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "cardWithdrawNotify");
    }

    /**
     * 卡充值通知
     * @param entity
     * @param merchant
     */
    public void cardChargeNotify(JDepositEntity entity, JMerchantEntity merchant){
        CardChargeNotify notify = ConvertUtils.sourceToTarget(entity, CardChargeNotify.class);
        apiNotifyService.notifyMerchant(notify, merchant, "cardChargeNotify");
    }
}
