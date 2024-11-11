package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
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

    // 预付费卡 单笔提现(调整主卡可用额度)
    public void ledgePrepaidWithdraw(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAdjustAmount().negate();
        String factMemo = String.format("预付费卡提现:%s", factAmount);

        JBalanceEntity prepaidSumBalance = ledgerUtil.getPrepaidSumAccount(cardEntity.getId(), cardEntity.getCurrency());

        // 主卡额度+
        ledgerUtil.ledgeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_WITHDRAW, LedgerConstant.FACT_PREPAID_WITHDRAW_UP, entity.getId(), factMemo, factAmount);

        // 发卡总额-
        ledgerUtil.ledgeUpdate(prepaidSumBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_WITHDRAW, LedgerConstant.FACT_PREPAID_WITHDRAW_DOWN, entity.getId(), factMemo, factAmount.negate());
    }
}
