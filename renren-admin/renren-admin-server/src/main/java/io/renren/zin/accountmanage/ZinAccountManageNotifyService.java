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
import io.renren.zcommon.ZinConstant;
import io.renren.zin.accountmanage.dto.TMoneyInNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
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
    // body:[{"acctno":"80000000369966","amount":100000,"applyid":"461809","bid":"2024110903652310","currency":"USD","nid":"20241109000000489344","payeraccountname":"自营商户","payeraccountno":"12312113","time":"2024-11-09T11:07:10Z","trxcod":"CP213"}]
    private void withApply(TMoneyInNotify notify) {
        log.info("入金申请, 申请单号: {}", notify.getApplyid());

        // 直接去匹配申请单
        JMoneyEntity jMoneyEntity = jMoneyDao.selectOne(Wrappers.<JMoneyEntity>lambdaQuery()
                .eq(JMoneyEntity::getApplyid, notify.getApplyid())
        );
        if (jMoneyEntity == null) {
            throw new RenException("入金通知, applyid=" + notify.getApplyid() + "无法找到原来申请单");
        }
        // 如果能找到, 看来账账号是否匹配
        if (!notify.getPayeraccountno().equals(jMoneyEntity.getCardno())) {
            log.error("无法匹配账号:{} -> {}", notify.getPayeraccountno(), jMoneyEntity.getCardno());
            throw new RenException("入金通知, applyid=" + notify.getApplyid() + ", 来账账号不匹配, notify:" + notify.getPayeraccountno() + ",db:" + jMoneyEntity.getCardno());
        }

        // 匹配金额
        if(notify.getAmount().compareTo(jMoneyEntity.getApplyAmount()) != 0) {
            log.error("无法匹配金额:{} -> {}", notify.getAmount(), jMoneyEntity.getAmount());
            throw new RenException("入金通知, applyid=" + notify.getApplyid() + ", 金额不匹配, notify:" + notify.getAmount() + ",db:" + jMoneyEntity.getAmount());
        }

        log.info("匹配到入金申请单成功, 开始记账...");
        // 记账 + 更新记录
        tx.executeWithoutResult(status -> {
            int cnt = jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                    .eq(JMoneyEntity::getId, jMoneyEntity.getId())
                    .ne(JMoneyEntity::getState, ZinConstant.PAY_APPLY_LG_DJ)  // 没记账
                    .set(JMoneyEntity::getState, ZinConstant.PAY_APPLY_LG_DJ) // 记账
                    .set(JMoneyEntity::getPayeraccountno, notify.getPayeraccountno())
                    .set(JMoneyEntity::getPayeraccountbank, notify.getPayeraccountbank())
                    .set(JMoneyEntity::getPayeraccountcountry, notify.getPayeraccountcountry())
                    .set(JMoneyEntity::getBid, notify.getBid())
                    .set(JMoneyEntity::getNid, notify.getNid())
                    .set(JMoneyEntity::getAmount, notify.getAmount())
                    .set(JMoneyEntity::getStatDate, new Date())
            );
            if (cnt != 1) {
                throw new RenException("入金通知处理失败");
            }

            // 重新查回来
            JMoneyEntity entity = jMoneyDao.selectById(jMoneyEntity.getId());
            try {
                ledgerMoneyIn.ledgeMoneyIn(entity);
            } catch (Exception ex) {
                log.error("记账失败, 入金记录:{}", entity);
                ex.printStackTrace();
                throw ex;
            }
        });

        // 是接口操作, 需要通知商户: todo
        if (jMoneyEntity.getApi().equals(1)) {
            // todo
        }
        return;
    }

    // 入账通知: 6003:
    public void handle(TMoneyInNotify notify) {
        // CP213
        if (notify.getTrxcod().equals(ZinConstant.CP213)) {
            // 没有申请单号
            if (notify.getApplyid() == null) {
                JMoneyEntity notifyInsert = ConvertUtils.sourceToTarget(notify, JMoneyEntity.class);
                jMoneyDao.insert(notifyInsert);
                // 匹配到商户
                this.match(notifyInsert);
                return;
            }

            // 没有申请单
            this.withApply(notify);
            return;
        }

        if (notify.getTrxcod().equals(ZinConstant.CP201)) {
            log.info("CP201 todo");
            throw new RenException("CP201尚未实现");
        }
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
                    .set(JMoneyEntity::getStatDate, new Date())
            );
            entity.setMerchantId(finalMerchantId);
            entity.setMerchantName(finalMerchantName);
            ledgerMoneyIn.ledgeMoneyIn(entity);
        });

        return true;
    }

}
