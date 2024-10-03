package io.renren.zapi.notifyevent;

import org.springframework.context.ApplicationEvent;

public class VaDepositNotifyEvent extends ApplicationEvent {
    private final Long moneyId;

    public VaDepositNotifyEvent(Object source, Long moneyId) {
        super(source);
        this.moneyId = moneyId;
    }

    public Long getMoneyId() {
        return moneyId;
    }
}
