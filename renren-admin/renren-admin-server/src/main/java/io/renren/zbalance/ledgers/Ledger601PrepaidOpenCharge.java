package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class Ledger601PrepaidOpenCharge {
    // 预付子卡费卡-批量开卡充值: 需要操作对应主卡
    public static final int ORIGIN_TYPE_PREPAID_OPEN_CHARGE = 601;                   //钱包子卡批量开卡卡充值
    public static final int FACT_PREPAID_OPEN_CHARGE_FREEZE_PREPAID_QUOTA = 60100;   // 1. 预付费主卡额度 冻结
    public static final int FACT_PREPAID_OPEN_CHARGE_UNFREEZE_PREPAID_QUOTA = 60101; // 2. 预付费主卡额度 解冻
    public static final int FACT_PREPAID_OPEN_CHARGE_CONFIRM_PREPAID_QUOTA = 60102;  // 3. 预付费主卡额度 确认
    public static final int FACT_PREPAID_OPEN_CHARGE_IN_PREPAID_SUM = 60103;         // 4.钱包子卡发卡总额

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 预付费子卡-批量开卡充值(调整主卡可以额度)
    public void ledgePrepaidOpenChargeFreeze(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("冻结-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);

        // 记账
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.freezeUpdate(prepaidBalance, ORIGIN_TYPE_PREPAID_OPEN_CHARGE, FACT_PREPAID_OPEN_CHARGE_FREEZE_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);

    }

    // 预付费子卡 批量充值解冻(调整主卡可用额度)
    public void ledgePrepaidOpenChargeUnFreeze(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("解冻-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, ORIGIN_TYPE_PREPAID_OPEN_CHARGE, FACT_PREPAID_OPEN_CHARGE_UNFREEZE_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);
    }

    // 预付费子卡 批量开卡充值确认(调整主卡可用额度)
    public void ledgePrepaidOpenCharge(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("确认-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);

        // 记账1: 主卡额度-减
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.confirmUpdate(prepaidBalance, ORIGIN_TYPE_PREPAID_OPEN_CHARGE, FACT_PREPAID_OPEN_CHARGE_CONFIRM_PREPAID_QUOTA, entity.getId(), factMemo, factAmount);

        // 记账2: 预付费发卡总额-加
        JBalanceEntity prepaidSumBalance = ledgerUtil.getPrepaidSumAccount(cardEntity.getId(), cardEntity.getCurrency());
        ledgerUtil.ledgeUpdate(prepaidSumBalance, ORIGIN_TYPE_PREPAID_OPEN_CHARGE, FACT_PREPAID_OPEN_CHARGE_IN_PREPAID_SUM, entity.getId(), factMemo, factAmount);
    }
}
