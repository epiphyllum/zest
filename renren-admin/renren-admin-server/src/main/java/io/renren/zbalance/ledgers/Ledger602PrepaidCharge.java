package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
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
public class Ledger602PrepaidCharge {
    public static final int ORIGIN_TYPE_PREPAID_CHARGE = 602;                            //钱包子卡充值
    public static final int FACT_PREPAID_CHARGE_FREEZE_PREPAID_QUOTA = 60200;            // 1. 预付费主卡额度 冻结
    public static final int FACT_PREPAID_CHARGE_UNFREEZE_PREPAID_QUOTA = 60201;          // 2. 预付费主卡额度 解结.
    public static final int FACT_PREPAID_CHARGE_CONFIRM_PREPAID_QUOTA = 60202;           // 3. 预付费主卡额度 确认
    public static final int FACT_PREPAID_CHARGE_IN_PREPAID_SUM = 60203;                  // 4. 钱包子卡发卡总额

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 预付费子卡-单笔充值(调整主卡可以额度)
    public void ledgePrepaidChargeFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("冻结-单笔充值:%s", factAmount);

        // 记账
        JBalanceEntity prepaidQuota = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.freezeUpdate(prepaidQuota, ORIGIN_TYPE_PREPAID_CHARGE, FACT_PREPAID_CHARGE_FREEZE_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
    }

    // 预付费子卡 单笔充值解冻(调整主卡可用额度)
    public void ledgePrepaidChargeUnFreeze(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("解冻-单笔充值:%s", factAmount);

        // 记账
        JBalanceEntity prepaidQuota = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.unFreezeUpdate(prepaidQuota, ORIGIN_TYPE_PREPAID_CHARGE, FACT_PREPAID_CHARGE_UNFREEZE_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
    }

    // 预付费子卡 单笔开卡充值确认(调整主卡可用额度)
    public void ledgePrepaidCharge(JVpaAdjustEntity entity) {
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery().eq(JCardEntity::getCardno, maincardno));
        BigDecimal factAmount = entity.getAdjustAmount();
        String factMemo = String.format("确认-单笔充值:%s", factAmount);

        // 主卡额度
        JBalanceEntity prepaidQuota = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.confirmUpdate(prepaidQuota, ORIGIN_TYPE_PREPAID_CHARGE, FACT_PREPAID_CHARGE_CONFIRM_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);

        // 发卡总额
        JBalanceEntity prepaidSum = ledgerUtil.getPrepaidSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(prepaidSum, ORIGIN_TYPE_PREPAID_CHARGE, FACT_PREPAID_CHARGE_IN_PREPAID_SUM, entity.getId(), factMemo, factAmount);
    }
}
