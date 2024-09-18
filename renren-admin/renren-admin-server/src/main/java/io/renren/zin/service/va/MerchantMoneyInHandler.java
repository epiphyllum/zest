package io.renren.zin.service.va;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.service.va.dto.TMoneyInNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@Slf4j
public class MerchantMoneyInHandler {
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private Ledger ledger;
    @Resource
    private ZestConfig zestConfig;

    public void handle(TMoneyInNotify notify) {
        List<JMaccountEntity> jMaccountEntities = jMaccountDao.selectList(Wrappers.<JMaccountEntity>emptyWrapper());
        Long merchantId = null;
        String merchantName = null;

        // 匹配商户
        for (JMaccountEntity jMaccountEntity : jMaccountEntities) {
            if (notify.getPayeraccountno().equals(jMaccountEntity.getCardno())) {
                merchantId = jMaccountEntity.getMerchantId();
                merchantName = jMaccountEntity.getMerchantName();
                break;
            }
        }

        JMoneyEntity jMoneyEntity = ConvertUtils.sourceToTarget(notify, JMoneyEntity.class);
        jMoneyEntity.setMerchantId(zestConfig.getDeptId());

        // 无法匹配商户
        if (merchantId == null) {
            log.error("无法匹配商户来账账户[{}][{}]", notify.getPayeraccountname(), notify.getPayeraccountno());
            jMoneyEntity.setStatus(0);
            jMoneyDao.insert(jMoneyEntity);
            return;
        }

        // 补充匹配商户信息
        jMoneyEntity.setMerchantId(merchantId);
        jMoneyEntity.setMerchantName(merchantName);
        tx.executeWithoutResult(status -> {
            jMoneyDao.insert(jMoneyEntity);
            ledger.ledgeMoneyIn(jMoneyEntity);
        });
    }

    public boolean match(Long id) {
        JMoneyEntity entity = jMoneyDao.selectById(id);
        List<JMaccountEntity> jMaccountEntities = jMaccountDao.selectList(Wrappers.<JMaccountEntity>emptyWrapper());
        Long merchantId = null;
        String merchantName = null;

        // 匹配商户
        for (JMaccountEntity jMaccountEntity : jMaccountEntities) {
            if (entity.getPayeraccountno().equals(jMaccountEntity.getCardno())) {
                merchantId = jMaccountEntity.getMerchantId();
                merchantName = jMaccountEntity.getMerchantName();
                break;
            }
        }

        if (merchantId == null) {
            log.error("无法匹配[{}][{}]", entity.getPayeraccountname(), entity.getPayeraccountno());
            return false;
        }

        Long finalMerchantId = merchantId;
        String finalMerchantName = merchantName;
        tx.executeWithoutResult(status -> {
            jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                    .eq(JMoneyEntity::getId, entity.getId())
                    .set(JMoneyEntity::getStatus, 1)
                    .set(JMoneyEntity::getMerchantId, finalMerchantId)
                    .set(JMoneyEntity::getMerchantName, finalMerchantName)
            );
            entity.setMerchantId(finalMerchantId);
            entity.setMerchantName(finalMerchantName);
            ledger.ledgeMoneyIn(entity);
        });

        return true;
    }
}
