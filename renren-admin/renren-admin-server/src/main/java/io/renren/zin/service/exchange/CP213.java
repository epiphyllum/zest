package io.renren.zin.service.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


// CP213  伞形账户入账
@Service
public class CP213 {

    @Resource
    private JMoneyDao jMoneyDao;

    public void handle(TExchangeStateNotify notify, int status) {
        jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                .eq(JMoneyEntity::getApplyid, notify.getApplyid())
                .set(JMoneyEntity::getState, notify.getState())
        );
    }
}
