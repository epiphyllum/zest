package io.renren.zapi.notifyevent;

import org.springframework.context.ApplicationEvent;

public class CardApplyNotifyEvent extends ApplicationEvent {
    private final Long cardId;

    public CardApplyNotifyEvent(Object source, Long cardId) {
        super(source);
        this.cardId = cardId;
    }

    public Long getCardId() {
        return cardId;
    }
}
