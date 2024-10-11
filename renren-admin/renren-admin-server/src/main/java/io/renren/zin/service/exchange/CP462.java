package io.renren.zin.service.exchange;

import io.renren.zadmin.dao.JDepositDao;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


// CP462  释放担保金
@Service
public class CP462 {

    @Resource
    private JDepositDao jDepositDao;

    // todo:
    public void handle(TExchangeStateNotify notify) {
    }
}
