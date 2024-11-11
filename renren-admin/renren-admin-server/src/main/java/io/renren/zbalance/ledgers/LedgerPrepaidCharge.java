package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LedgerPrepaidCharge {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 预付费卡-单笔充值(调整主卡可以额度)
    public void ledgePrepaidChargeFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-单笔充值:%s", factAmount);
        ledgerUtil.freezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_CHARGE, LedgerConstant.FACT_PREPAID_CHARGE_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 预付费卡 单笔充值解冻(调整主卡可用额度)
    public void ledgePrepaidChargeUnFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("解冻-单笔充值:%s", factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_CHARGE, LedgerConstant.FACT_PREPAID_CHARGE_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 预付费卡 单笔开卡充值确认(调整主卡可用额度)
    public void ledgePrepaidCharge(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );

        // 金额
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAdjustAmount();

        // 主卡额度
        String factMemo = String.format("确认-单笔充值:%s", factAmount);
        ledgerUtil.confirmUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_CHARGE, LedgerConstant.FACT_PREPAID_CHARGE_CONFIRM, entity.getId(), factMemo, factAmount);

        // 发卡总额
        JBalanceEntity prepaidSumBalance = ledgerUtil.getPrepaidSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(prepaidSumBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_CHARGE, LedgerConstant.FACT_PREPAID_CHARGE_IN, entity.getId(), factMemo, factAmount);
    }
}
