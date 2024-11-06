package io.renren.zin.accountmanage;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zapi.ApiNotify;
import io.renren.zbalance.ledgers.LedgerMoneyIn;
import io.renren.zin.accountmanage.dto.TMoneyInNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    private LedgerMoneyIn ledgerMoneyIn;
    @Resource
    private ApiNotify apiNotify;

    // 有申请单的情况
    private void withApply(TMoneyInNotify notify) {
        // 直接去匹配申请单
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
            ledgerMoneyIn.ledgeMoneyIn(entity);
        });

        // 是接口操作, 需要通知商户: todo
        if (jMoneyEntity.getApi().equals(1)) {
            // todo
        }
        return;
    }

    // 入账通知: 6003:
    public void handle(TMoneyInNotify notify) {
        // 没有申请单
        if (notify.getApplyid() == null) {
            JMoneyEntity notifyInsert = ConvertUtils.sourceToTarget(notify, JMoneyEntity.class);
            jMoneyDao.insert(notifyInsert);
            // 匹配到商户
            this.match(notifyInsert);
            return;
        }
        // 有申请单
        this.withApply(notify);
    }

    // 账户匹配来账到子商户
    public boolean match(JMoneyEntity entity) {
        List<JMaccountEntity> jMaccountEntities = jMaccountDao.selectList(Wrappers.<JMaccountEntity>emptyWrapper());
        Long merchantId = null;
        String merchantName = null;
        Long agentId = null;
        String agentName = null;
        // 匹配商户
        for (JMaccountEntity jMaccountEntity : jMaccountEntities) {
            if (entity.getPayeraccountno().equals(jMaccountEntity.getCardno())) {
                agentId = jMaccountEntity.getAgentId();
                agentName = jMaccountEntity.getAgentName();
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
        Long finalAgentId = agentId;
        String finalAgentName = agentName;
        tx.executeWithoutResult(status -> {
            jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                    .eq(JMoneyEntity::getId, entity.getId())
                    .set(JMoneyEntity::getState, 1)
                    .set(JMoneyEntity::getAgentId, finalAgentId)
                    .set(JMoneyEntity::getAgentName, finalAgentName)
                    .set(JMoneyEntity::getMerchantId, finalMerchantId)
                    .set(JMoneyEntity::getMerchantName, finalMerchantName)
            );
            entity.setMerchantId(finalMerchantId);
            entity.setMerchantName(finalMerchantName);
            ledgerMoneyIn.ledgeMoneyIn(entity);
        });
        return true;
    }

}
