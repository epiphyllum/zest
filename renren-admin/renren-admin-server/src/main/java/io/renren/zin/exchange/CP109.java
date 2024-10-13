package io.renren.zin.exchange;

import io.renren.zin.exchange.dto.TExchangeStateNotify;
import org.springframework.stereotype.Service;

// CP109 离岸下发: 其实就是钱提走到体系外
@Service
public class CP109 {
    public void handle(TExchangeStateNotify notify) {
    }
}
