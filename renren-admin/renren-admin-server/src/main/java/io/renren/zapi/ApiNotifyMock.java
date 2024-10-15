package io.renren.zapi;

import io.renren.zcommon.ZapiConstant;
import org.springframework.stereotype.Service;

@Service
public class ApiNotifyMock {
    public Object handle(String name) {
        switch (name) {
            case ZapiConstant.API_subNotify:
                return this.subNotify();
            case ZapiConstant.API_cardChangeNotify:
                return this.cardChangeNotify();
            case ZapiConstant.API_cardChargeNotify:
                return this.cardChargeNotify();
            case ZapiConstant.API_cardNewNotify:
                return this.cardNewNotify();
            case ZapiConstant.API_cardTxnNotify:
                return this.cardTxnNotify();
            case ZapiConstant.API_cardWithdrawNotify:
            case ZapiConstant.API_exchangeNotify:
                return exchangeNotify();
            case ZapiConstant.API_moneyAccountNotify:
                return moneyAccountNotify();
            case ZapiConstant.API_moneyNotify:
                return moneyNotify();
        }
        return null;
    }

    private Object cardChargeNotify() {
        return null;
    }

    private Object cardTxnNotify() {
        return null;
    }

    private Object exchangeNotify() {
        return null;
    }

    private Object moneyAccountNotify() {
        return null;
    }

    private Object moneyNotify() {
        return null;
    }

    private Object cardNewNotify() {
        return null;
    }

    private Object cardChangeNotify() {
        return null;
    }

    private Object subNotify() {
        return null;
    }

}
