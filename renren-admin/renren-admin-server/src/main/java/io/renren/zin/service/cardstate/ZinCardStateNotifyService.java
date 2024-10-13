package io.renren.zin.service.cardstate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.manager.JCardManager;
import io.renren.manager.JMcardManager;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zin.service.cardstate.dto.TCardChangeNotify;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ZinCardStateNotifyService {

    @Resource
    private JCardManager jCardManager;
    @Resource
    private JMcardManager jMcardManager;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JMcardDao jMcardDao;

    // 3207-卡状态变更通知, 卡状态变更，主动发起通知合作方，涉及的状态变更的功能接口
    // 1. 卡挂失
    // 2. 解除卡挂失
    // 3. 卡止付
    // 4. 解除卡止付
    // 5. 销卡
    // 6. 解除销卡
    public void handle(TCardChangeNotify notify) {
        JCardEntity subCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, notify.getCardno()));
        if (subCardEntity == null) {
            JMcardEntity mainCardEntity = jMcardDao.selectOne(Wrappers.<JMcardEntity>lambdaQuery().eq(JMcardEntity::getCardno, notify.getCardno()));
            jMcardManager.queryCard(mainCardEntity);
            return;
        }
        jCardManager.queryCard(subCardEntity);
    }
}
