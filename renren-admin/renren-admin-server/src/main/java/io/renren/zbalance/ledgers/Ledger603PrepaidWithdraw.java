package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class Ledger603PrepaidWithdraw {
    //预付费卡-提现: 操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_WITHDRAW = 603;                          //钱包子卡提现
    public static final int FACT_PREPAID_WITHDRAW_IN_PREPAID_QUOTA = 60300;              // 1: 调增主卡剩余额度
    public static final int FACT_PREPAID_WITHDRAW_OUT_PREPAID_SUM = 60301;               // 2: 发卡总额减

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    //预付费子卡 单笔提现(调整主卡可用额度)
    public void ledgePrepaidWithdraw(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount().negate();
        String factMemo = String.format("预付费卡提现:%s", factAmount);

        // 主卡额度+
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(prepaidBalance, ORIGIN_TYPE_PREPAID_WITHDRAW, FACT_PREPAID_WITHDRAW_IN_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);

        // 发卡总额-
        JBalanceEntity prepaidSum = ledgerUtil.getPrepaidSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(prepaidSum, ORIGIN_TYPE_PREPAID_WITHDRAW, FACT_PREPAID_WITHDRAW_OUT_PREPAID_SUM, entity.getId(), factMemo, factAmount.negate());
    }
}
