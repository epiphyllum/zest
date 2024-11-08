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
public class LedgerPrepaidOpenCharge {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 预付费卡-批量开卡充值(调整主卡可以额度)
    public void ledgePrepaidOpenChargeFreeze(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("冻结-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        ledgerUtil.freezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_OPEN_CHARGE, LedgerConstant.FACT_PREPAID_OPEN_CHARGE_FREEZE, entity.getId(), factMemo, factAmount);

    }

    // 预付费卡 批量充值解冻(调整主卡可用额度)
    public void ledgePrepaidOpenChargeUnFreeze(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("解冻-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_OPEN_CHARGE, LedgerConstant.FACT_PREPAID_OPEN_CHARGE_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 预付费卡 批量开卡充值确认(调整主卡可用额度)
    public void ledgePrepaidOpenCharge(JVpaJobEntity entity) {
        // 找到主卡
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("确认-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        ledgerUtil.confirmUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_OPEN_CHARGE, LedgerConstant.FACT_PREPAID_OPEN_CHARGE_CONFIRM, entity.getId(), factMemo, factAmount);
    }
}
