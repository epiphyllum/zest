package io.renren.zin.service.cardapply;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZinConstant;
import io.renren.zin.service.cardapply.dto.TCardApplyNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

/**
 * 用以在主卡/子卡申请、缴纳保证金、提取保证金、卡注销申请单状态变化时通知合作方。
 */
@Service
@Slf4j
public class CardApplyNotify {

    @Resource
    private JCardDao jCardDao;
    @Resource
    private Ledger ledger;
    @Resource
    private TransactionTemplate tx;

    public void handle(TCardApplyNotify notify) {
        String trxcode = notify.getTrxcode();
        if (trxcode.equals(ZinConstant.CP450)) {
            handleCP450(notify);
        } else if (trxcode.equals(ZinConstant.CP453)) {
            handleCP453(notify);
        } else if (trxcode.equals(ZinConstant.CP458)) {
            handleCP458(notify);
        } else if (trxcode.equals(ZinConstant.CP451)) {
            handleCP451(notify);
        } else if (trxcode.equals(ZinConstant.CP452)) {
            handleCP452(notify);
        } else if (trxcode.equals(ZinConstant.CP462)) {
            handleCP452(notify);
        } else {
            log.error("unknown notify: {}", notify);
        }
    }

    // CP450	开卡	申请开卡
    public void handleCP450(TCardApplyNotify notify) {
        JCardEntity jCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getApplyid, notify.getApplyid())
        );
        tx.executeWithoutResult(status -> {
            jCardDao.update(null, Wrappers.<JCardEntity>lambdaUpdate()
                    .eq(JCardEntity::getId, jCardEntity.getId())
                    .eq(JCardEntity::getState, jCardEntity.getState())  // 确保中间没有状态变化
                    .set(JCardEntity::getState, jCardEntity.getState())
                    .set(JCardEntity::getCardno, notify.getCardno())
                    .set(JCardEntity::getFee, notify.getFee())
                    .set(JCardEntity::getFeecurrency, notify.getFeecurrency())
            );
            if (!notify.getFee().equals(BigDecimal.ZERO) && notify.getState().equals(ZinConstant.CARD_APPLY_SUCCESS)) {
                JCardEntity entity = jCardDao.selectById(jCardEntity.getId());
                ledger.ledgeOpenCard(entity);
            }
        });
    }

    // CP453	注销	卡的注销/撤回注销
    public void handleCP453(TCardApplyNotify notify) {
    }

    // CP458	注销撤回
    public void handleCP458(TCardApplyNotify notify) {
    }

    // CP451	保证金缴纳	卡片资金充值
    public void handleCP451(TCardApplyNotify notify) {
    }

    //CP452	保证金提现	卡片资金提现
    public void handleCP452(TCardApplyNotify notify) {
    }

    // CP462	释放担保金
    public void handleCP462(TCardApplyNotify notify) {
    }

}
