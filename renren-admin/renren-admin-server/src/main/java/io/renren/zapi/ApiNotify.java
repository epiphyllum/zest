package io.renren.zapi;

import io.renren.zapi.account.ApiAccountNotifyService;
import io.renren.zapi.allocate.ApiAllocateNotifyService;
import io.renren.zapi.cardapply.ApiCardApplyNotifyService;
import io.renren.zapi.cardmoney.ApiCardMoneyNotifyService;
import io.renren.zapi.cardstate.ApiCardStateNotifyService;
import io.renren.zapi.exchange.ApiExchangeNotifyService;
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

    // 子商户创建通知
    public void subNotify() {
    }
    // 来账白名单通知
    public void moneyAccountNotify() {
    }
    // 入金通知
    public void moneyNotify() {
    }
    // 开卡通知
    public void cardNewNotify() {
    }
    // 卡充值通知
    public void cardChargeNotify() {
    }
    // 卡提现通知
    public void cardWithdrawNotify() {
    }
    // 商户卡交易通知
    public void cardTxnNotify() {
    }
    // 换汇通知
    public void exchangeNotify() {
    }
}
