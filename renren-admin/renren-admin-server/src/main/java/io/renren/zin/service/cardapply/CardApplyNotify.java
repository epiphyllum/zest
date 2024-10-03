package io.renren.zin.service.cardapply;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zapi.notifyevent.CardApplyNotifyEvent;
import io.renren.zapi.service.card.ApiCardService;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZinConstant;
import io.renren.zin.service.cardapply.dto.TCardApplyNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private JMcardDao jMcardDao;
    @Resource
    private Ledger ledger;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private ApplicationEventPublisher publisher;

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

    // 收到主卡申请通知
    public void handleCP450MainCard(TCardApplyNotify notify) {
        JMcardEntity jMcardEntity = jMcardDao.selectOne(Wrappers.<JMcardEntity>lambdaQuery()
                .eq(JMcardEntity::getApplyid, notify.getApplyid())
        );
        // 状态一样
        if (jMcardEntity.getState().equals(notify.getState())) {
            return;
        }

        tx.executeWithoutResult(status -> {
            jMcardDao.update(null, Wrappers.<JMcardEntity>lambdaUpdate()
                    .eq(JMcardEntity::getId, jMcardEntity.getId())
                    .eq(JMcardEntity::getState, jMcardEntity.getState())  // 确保中间没有状态变化
                    .set(JMcardEntity::getState, notify.getState())
                    .set(JMcardEntity::getCardno, notify.getCardno())
                    .set(JMcardEntity::getFee, notify.getFee())
                    .set(JMcardEntity::getFeecurrency, notify.getFeecurrency())
            );
        });
    }

    // 收到子卡申请通知
    public void handleCP450SubCard(TCardApplyNotify notify) {
        JCardEntity jCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getApplyid, notify.getApplyid())
        );

        // 状态一样: 不需要处理
        if (jCardEntity.getState().equals(notify.getState())) {
            return;
        }

        // 准备待更新字段
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setState(notify.getState());
        update.setFeecurrency(notify.getFeecurrency());
        update.setFee(notify.getFee());
        update.setCardno(notify.getCardno());

        // 从非失败  -> 失败,  处理退款
        String prevState = jCardEntity.getState();
        String nextState = notify.getState();
        if (!(prevState.equals(ZinConstant.CARD_APPLY_VERIFY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_CLOSE)
        ) && (nextState.equals(ZinConstant.CARD_APPLY_VERIFY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_CLOSE))
        ) {
            tx.executeWithoutResult(status -> {
                jCardDao.updateById(update);
                ledger.ledgeOpenCardFail(jCardEntity);
            });
        } else {
            // 其他情况， 只需要更新状态
            jCardDao.updateById(update);
        }

        // 不是API就不用通知接入商户了
        if (jCardEntity.getApi().equals(0)) {
            return;
        }

        // 通知接入商户
        publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
    }

    // CP450	开卡	申请开卡
    public void handleCP450(TCardApplyNotify notify) {
        String cardbusinesstype = notify.getCardbusinesstype();
        // 主卡
        if (cardbusinesstype.equals("1")) {
            this.handleCP450MainCard(notify);
        }

        // 子卡
        if (cardbusinesstype.equals("1")) {
            this.handleCP450SubCard(notify);
        }
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
