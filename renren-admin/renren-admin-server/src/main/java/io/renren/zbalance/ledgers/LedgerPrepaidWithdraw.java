package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LedgerPrepaidWithdraw {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 预付费卡-单笔充值(调整主卡可以额度)
    public void ledgePrepaidWithdrawFreeze(JDepositEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAmount();
        String factMemo = String.format("冻结-预付费卡提现:%s", factAmount);
        ledgerUtil.freezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_WITHDRAW, LedgerConstant.FACT_PREPAID_WITHDRAW_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 预付费卡 单笔充值解冻(调整主卡可用额度)
    public void ledgePrepaidWithdrawUnFreeze(JDepositEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAmount();
        String factMemo = String.format("解冻-预付费卡提现:%s", factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_WITHDRAW, LedgerConstant.FACT_PREPAID_WITHDRAW_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 预付费卡 单笔开卡充值确认(调整主卡可用额度)
    public void ledgePrepaidWithdraw(JDepositEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAmount();
        String factMemo = String.format("确认-预付费卡提现:%s", factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_WITHDRAW, LedgerConstant.FACT_PREPAID_WITHDRAW_CONFIRM, entity.getId(), factMemo, factAmount);
    }
}
