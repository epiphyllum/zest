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

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "卡资金提取:" + showAmount;
        BigDecimal factAmount = entity.getAmount();

        // 记账1: 子商户-Va:  amount + abs(merchantfee)
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_IN_SUB_VA, entity.getId(), factMemo, factAmount.add(entity.getMerchantfee().negate()));

        // 记账2: 子商户-卡汇总资金
        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(cardSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_CARD_SUM, entity.getId(), factMemo, factAmount.negate());
        // 记账2: 通联-卡充值汇总
        JBalanceEntity aipCardSum = ledgerUtil.getAipCardSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCardSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_AIP_CARD_SUM, entity.getId(), factMemo, entity.getAmount().negate());

        // 记账3: 子商户-手续费汇总 merchantfee已经是负数
        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(charge, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_CARD_CHARGE, entity.getId(), factMemo, entity.getMerchantfee());
        // 记账3: 通联-手续费汇总  fee已经是负数
        JBalanceEntity aipCharge = ledgerUtil.getAipChargeAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(aipCharge, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_AIP_CHARGE, entity.getId(), factMemo, entity.getFee());

        // 预付费主卡提现
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.ledgeUpdate(ppMain, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_OUT_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
        }
    }
}
