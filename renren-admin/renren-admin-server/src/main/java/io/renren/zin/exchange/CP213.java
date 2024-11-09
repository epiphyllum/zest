package io.renren.zin.exchange;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zin.exchange.dto.TExchangeStateNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

// CP213  伞形账户入账
@Service
public class CP213 {
    @Resource
    private JMoneyDao jMoneyDao;

    //  对我们来说只是更新下中间状态
    public void handle(TExchangeStateNotify notify) {
        jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                .eq(JMoneyEntity::getApplyid, notify.getApplyid())
                .set(JMoneyEntity::getState, notify.getState())
                .set(JMoneyEntity::getStateexplain, notify.getStateexplain())
        );
    }
}
