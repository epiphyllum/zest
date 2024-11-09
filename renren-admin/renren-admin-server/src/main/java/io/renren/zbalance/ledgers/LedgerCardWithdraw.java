package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.*;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LedgerCardWithdraw {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 卡资金退回: 冻结
    public void ledgeCardWithdrawFreeze(JWithdrawEntity entity, JSubEntity sub) {
        String factMemo = "冻结-卡资金提取:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE, entity.getId(), factMemo, factAmount);

        // 预付费主卡提现
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {

            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
            if( entity.getAmount().compareTo(ppMain.getBalance()) > 0) {
                throw new RenException("预付费主卡可用额度不足:" + ppMain.getBalance());
            }
            ledgerUtil.freezeUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE_PREPAID_MAIN, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡资金退回: 解冻
    public void ledgeCardWithdrawUnFreeze(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        String factMemo = "解冻-确认卡资金提取:" + BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP);
        ledgerUtil.unFreezeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_UN_FREEZE, entity.getId(), factMemo, factAmount);
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.unFreezeUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_UN_FREEZE_PREPAID_MAIN, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JWithdrawEntity entity, JSubEntity sub) {
        // 子商户va, 卡汇总充值资金账号
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-卡资金提取:" + showAmount;
        BigDecimal factAmount = entity.getAmount();
        // 记账1: 子商户Va-confirm
        ledgerUtil.confirmUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_CONFIRM, entity.getId(), factMemo, factAmount);
        // 记账2: 子商户Va+
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_IN, entity.getId(), factMemo, factAmount);

        // 预付费主卡提现
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.confirmUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_PREPAID_MAIN, entity.getId(), factMemo, factAmount);
        }

    }

}
