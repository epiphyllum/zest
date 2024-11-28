package io.renren.zin.cardtxn;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiNotify;
import io.renren.zapi.ApiNotifyService;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JCardManager;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zin.cardtxn.dto.TAuthTxnNotify;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
public class ZinCardTxnNotifyService {

    @Resource
    private JAuthDao jAuthDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private ApiNotify apiNotify;

    // 4002-授权交易通知:  包含了
    // 1. 消费流水,
    // 2. 卡充值记录
    // 3. 提现记录
    public void handle(TAuthTxnNotify notify) {
        // copy
        JAuthEntity entity = ConvertUtils.sourceToTarget(notify, JAuthEntity.class);

        JCardEntity card = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, notify.getCardno())
        );
        Long subId = card.getSubId();
        JSubEntity subEntity = jSubDao.selectById(subId);
        // 打标签
        entity.setAgentId(subEntity.getAgentId());
        entity.setAgentName(subEntity.getAgentName());
        entity.setMerchantId(subEntity.getMerchantId());
        entity.setMerchantName(subEntity.getMerchantName());
        entity.setSubId(subId);
        entity.setSubName(subEntity.getCusname());
        entity.setMarketproduct(card.getMarketproduct());
        entity.setCreateDate(new Date());

        // 如果是-钱包卡
        if (card.getMarketproduct().equals(ZinConstant.MP_VPA_WALLET)) {
            entity.setWalletId(card.getWalletId());
            entity.setMaincardno(card.getMaincardno());
        }

        Long id = DefaultIdentifierGenerator.getInstance().nextId(entity);
        entity.setId(id);
        jAuthDao.saveOrUpdate(entity);

        // 更新下余额
        CompletableFuture.runAsync(() -> {
            jCardManager.balanceCard(card);
        });

        // 通知商户
        entity = jAuthDao.selectById(id);
        JMerchantEntity merchant = jMerchantDao.selectById(subEntity.getMerchantId());

        // 通知商户
        if (StringUtils.isNotBlank(merchant.getWebhook())) {
            apiNotify.cardTxnNotify(entity, merchant);
        }
    }
}
