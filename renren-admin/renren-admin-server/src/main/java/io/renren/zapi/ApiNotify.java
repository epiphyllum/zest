package io.renren.zapi;

import io.renren.zadmin.entity.*;
import io.renren.zapi.account.ApiAccountNotifyService;
import io.renren.zapi.allocate.ApiAllocateNotifyService;
import io.renren.zapi.cardapply.ApiCardApplyNotifyService;
import io.renren.zapi.cardmoney.ApiCardMoneyNotifyService;
import io.renren.zapi.cardstate.ApiCardStateNotifyService;
import io.renren.zapi.exchange.ApiExchangeNotifyService;
import io.renren.zapi.sub.ApiSubNotifyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiNotify {
    @Resource
    private ApiAccountNotifyService apiAccountNotifyService;
    @Resource
    private ApiAllocateNotifyService apiAllocateNotifyService;
    @Resource
    private ApiCardApplyNotifyService apiCardApplyNotifyService;
    @Resource
    private ApiCardMoneyNotifyService apiCardMoneyNotifyService;
    @Resource
    private ApiCardStateNotifyService apiCardStateNotifyService;
    @Resource
    private ApiExchangeNotifyService apiExchangeNotifyService;
    @Resource
    private ApiSubNotifyService apiSubNotifyService;

    // 子商户创建通知
    public void subNotify(JSubEntity entity, JMerchantEntity merchant) {
        apiSubNotifyService.subNotify(entity, merchant);
    }
    // 来账白名单通知
    public void moneyAccountNotify(JMaccountEntity entity, JMerchantEntity merchant) {
        apiAccountNotifyService.moneyAccountNotify(entity, merchant);
    }
    // 入金通知
    public void moneyNotify(JMoneyEntity entity, JMerchantEntity merchant) {
        apiAllocateNotifyService.moneyNotify(entity, merchant);
    }
    // 开卡通知
    public void cardNewNotify(JCardEntity entity, JMerchantEntity merchant) {
        apiCardApplyNotifyService.cardNewNotify(entity, merchant);
    }

    // 卡状态通知(发卡成功后的卡状态cardState)
    public void cardChangeNotify(JCardEntity entity, JMerchantEntity merchant) {
        apiCardStateNotifyService.cardChangeNotify(entity, merchant);
    }

    // 卡充值通知
    public void cardChargeNotify(JDepositEntity entity, JMerchantEntity merchant) {
        apiCardMoneyNotifyService.cardChargeNotify(entity, merchant);
    }
    // 卡提现通知
    public void cardWithdrawNotify(JWithdrawEntity entity, JMerchantEntity merchant) {
        apiCardMoneyNotifyService.cardWithdrawNotify(entity, merchant);
    }
    // 商户卡交易通知
    public void cardTxnNotify(JAuthEntity entity, JMerchantEntity merchant) {
        apiCardMoneyNotifyService.cardTxnNotify(entity, merchant);
    }
    // 换汇通知
    public void exchangeNotify(JExchangeEntity entity, JMerchantEntity merchant) {
        apiExchangeNotifyService.exchangeNotify(entity, merchant);
    }
}
