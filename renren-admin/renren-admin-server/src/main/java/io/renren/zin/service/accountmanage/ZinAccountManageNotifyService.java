package io.renren.zin.service.accountmanage;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zapi.notifyevent.VaDepositNotifyEvent;
import io.renren.zbalance.Ledger;
import io.renren.zin.service.accountmanage.dto.TMoneyInNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@Slf4j
public class ZinAccountManageNotifyService {
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private Ledger ledger;
    @Resource
    private ApplicationEventPublisher publisher;

    // 入账通知: 6003:
    public void moneyInNotify(TMoneyInNotify notify) {
        // 直接去匹配申请单
        if (notify.getApplyid() != null) {
            JMoneyEntity jMoneyEntity = jMoneyDao.selectOne(Wrappers.<JMoneyEntity>lambdaQuery()
                    .eq(JMoneyEntity::getApplyid, notify.getApplyid())
            );
            if (jMoneyEntity == null) {
                throw new RenException("入金通知, applyid=" + notify.getApplyid() + "无法找到原来申请单");
            }

            // 如果能找到, 看来账账号是否匹配
            if (!notify.getPayeraccountno().equals(jMoneyEntity.getCardno())) {
                throw new RenException("入金通知, applyid=" + notify.getApplyid() + ", 来账账号不匹配, notify:" + notify.getPayeraccountno() + "db:" + jMoneyEntity.getCardno());
            }

            // 更新入金通知
            JMoneyEntity updateEntity = ConvertUtils.sourceToTarget(notify, JMoneyEntity.class);
            updateEntity.setId(jMoneyEntity.getId());

            // 记账 + 更新记录
            tx.executeWithoutResult(status -> {
                jMoneyDao.updateById(updateEntity);
                JMoneyEntity entity = jMoneyDao.selectById(jMoneyEntity.getId());
                ledger.ledgeMoneyIn(entity);
            });

            // 是接口操作, 需要通知商户: todo
            if (jMoneyEntity.getApi().equals(1)) {
                publisher.publishEvent(new VaDepositNotifyEvent(this, null));
            }
            return;
        }

        // 通知没有申请单:  直接插入
        JMoneyEntity notifyInsert = ConvertUtils.sourceToTarget(notify, JMoneyEntity.class);
        jMoneyDao.insert(notifyInsert);
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
