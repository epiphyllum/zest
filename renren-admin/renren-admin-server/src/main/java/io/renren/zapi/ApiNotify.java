package io.renren.zapi;

import io.renren.zadmin.entity.*;
import io.renren.zapi.account.ApiAccountNotifyService;
import io.renren.zapi.allocate.ApiAllocateNotifyService;
import io.renren.zapi.cardapply.ApiCardApplyNotifyService;
import io.renren.zapi.cardmoney.ApiCardMoneyNotifyService;
import io.renren.zapi.cardstate.ApiCardStateNotifyService;
import io.renren.zapi.exchange.ApiExchangeNotifyService;
import io.renren.zapi.sub.ApiSubNotifyService;
import io.renren.zapi.vpa.ApiVpaNotifyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
    @Resource
    private ApiVpaNotifyService apiVpaNotifyService;

    // 子商户创建通知
    public void subNotify(JSubEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|subNotify: {}", entity);
        apiSubNotifyService.subNotify(entity, merchant);
    }

    // 来账白名单通知
    public void moneyAccountNotify(JMaccountEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|moneyAccountNotify: {}", entity);
        apiAccountNotifyService.moneyAccountNotify(entity, merchant);
    }

    // 入金通知
    public void moneyNotify(JMoneyEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|moneyNotify: {}", entity);
        apiAllocateNotifyService.moneyNotify(entity, merchant);
    }

    // 开卡通知
    public void cardNewNotify(JCardEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|cardNewNotify: {}", entity);
        apiCardApplyNotifyService.cardNewNotify(entity, merchant);
    }

    // 卡状态通知(发卡成功后的卡状态cardState)
    public void cardChangeNotify(JCardEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|cardChangeNotify: {}", entity);
        apiCardStateNotifyService.cardChangeNotify(entity, merchant);
    }

    // 卡充值通知
    public void cardChargeNotify(JDepositEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|cardChargeNotify: {}", entity);
        apiCardMoneyNotifyService.cardChargeNotify(entity, merchant);
    }

    // 卡提现通知
    public void cardWithdrawNotify(JWithdrawEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|cardWithdrawNotify: {}", entity);
        apiCardMoneyNotifyService.cardWithdrawNotify(entity, merchant);
    }

    // 商户卡交易通知
    public void cardTxnNotify(JAuthEntity entity, JMerchantEntity merchant) {
        apiCardMoneyNotifyService.cardTxnNotify(entity, merchant);
    }

    // 换汇通知
    public void exchangeNotify(JExchangeEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|exchangeNotify: {}", entity);
        apiExchangeNotifyService.exchangeNotify(entity, merchant);
    }

    // vpa开卡结果通知
    public void vpaJobNotify(JVpaJobEntity entity, JMerchantEntity merchant) {
        log.info("通知商户|vpaJobNotify: {}", entity);
        apiVpaNotifyService.vpaJobNotify(entity, merchant);
    }
}
