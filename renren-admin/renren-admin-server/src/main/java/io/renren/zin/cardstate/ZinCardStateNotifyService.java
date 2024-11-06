package io.renren.zin.cardstate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zmanager.JCardManager;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zin.cardstate.dto.TCardChangeNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ZinCardStateNotifyService {

    @Resource
    private JCardManager jCardManager;
    @Resource
    private JCardDao jCardDao;

    // 3207-卡状态变更通知, 卡状态变更，主动发起通知合作方，涉及的状态变更的功能接口
    // 1. 卡挂失
    // 2. 解除卡挂失
    // 3. 卡止付
    // 4. 解除卡止付
    // 5. 销卡
    // 6. 解除销卡
    public void handle(TCardChangeNotify notify) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, notify.getCardno()));
        jCardManager.queryCard(cardEntity);
    }
}
