package io.renren.zin.exchange;

import io.renren.zadmin.dao.JDepositDao;
import io.renren.zin.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

// CP462  释放担保金
@Service
public class CP462 {
    @Resource
    private JDepositDao jDepositDao;

    // 释放担保金处理
    public void handle(TExchangeStateNotify notify) {
    }
}
