package io.renren.zin.service.cardtxn;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.manager.JCardManager;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.service.JAuthService;
import io.renren.zin.service.cardstatus.ZinCardStatusService;
import io.renren.zin.service.cardtxn.dto.TAuthTxnNotify;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ZinCardTxnNotifyService {

    @Resource
    private JAuthDao jAuthDao;

    @Resource
    private JCardDao jCardDao;

    @Resource
    private JSubDao jSubDao;

    @Resource
    private JCardManager jCardManager;



    // 4002-授权交易通知
    public void handle(TAuthTxnNotify notify) {
        JAuthEntity entity = ConvertUtils.sourceToTarget(notify, JAuthEntity.class);
        JCardEntity card = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, notify.getCardno())
        );
        Long subId = card.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setSubId(subId);
        entity.setSubName(subEntity.getCusname());
        entity.setCreateDate(new Date());

        Long id = DefaultIdentifierGenerator.getInstance().nextId(entity);
        entity.setId(id);
        jAuthDao.saveOrUpdate(entity);

        // 更新下余额
        jCardManager.balanceCard(card);
    }
}