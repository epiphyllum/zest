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
        String maincardno = entity.getMaincardno();
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getCardno, maincardno)
        );
        JBalanceEntity prepaidBalance = ledgerUtil.getPrepaidAccount(cardEntity.getId(), cardEntity.getCurrency());
        BigDecimal factAmount = entity.getAuthmaxamount().multiply(new BigDecimal(entity.getNum()));
        String factMemo = String.format("确认-批量开通%s张预付费卡, 每张充值%s, 总充值:%s", entity.getNum(), entity.getAuthmaxamount(), factAmount);
        ledgerUtil.unFreezeUpdate(prepaidBalance, LedgerConstant.ORIGIN_TYPE_PREPAID_OPEN_CHARGE, LedgerConstant.FACT_PREPAID_OPEN_CHARGE_CONFIRM, entity.getId(), factMemo, factAmount);
    }




    // 开卡冻结
    public void ledgeOpenCardFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = "冻结开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡开解冻
    public void ledgeOpenCardUnFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = "解冻开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 原始凭证: 开卡费用
    public void ledgeOpenCard(JCardEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getCurrency());
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认开卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();
        // 子商户va扣除费用
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_CONFIRM, entity.getId(), factMemo, merchantFee);
        // 子商户开卡费用账户
        ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.ORIGIN_CARD_OPEN, LedgerConstant.FACT_CARD_OPEN_FEE_IN, entity.getId(), factMemo, merchantFee);
    }

    //
    public void ledgeCardChargeFreeze(JDepositEntity entity, JSubEntity sub) {
        String factMemo = "冻结卡充值:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 取消卡充值, 卡充值失败
    public void ledgeCardChargeUnFreeze(JDepositEntity entity, JSubEntity sub) {
        String factMemo = "解冻卡充值:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡充值
    public void ledgeCardCharge(JDepositEntity entity, JSubEntity sub) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal showAmount = BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认卡充值:" + showAmount;
        BigDecimal factAmount = entity.getAmount();
        // 记账1: 子商户subVa-
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_CONFIRM, entity.getId(), factMemo, factAmount);
        // 记账2: 子商户subSum+
        ledgerUtil.ledgeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_CHARGE, LedgerConstant.FACT_CARD_CHARGE_IN, entity.getId(), factMemo, factAmount);

        // 如果是给预付费主卡充值: todo
    }

    // 卡资金退回: 冻结
    public void ledgeCardWithdrawFreeze(JWithdrawEntity entity, JSubEntity sub) {
        String factMemo = "冻结卡资金提取:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡资金退回: 解冻
    public void ledgeCardWithdrawUnFreeze(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        String factMemo = "确认卡资金提取:" + BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP);
        ledgerUtil.unFreezeUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JWithdrawEntity entity, JSubEntity sub) {
        // 子商户va, 卡汇总充值资金账号
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认卡资金提取:" + showAmount;
        BigDecimal factAmount = entity.getAmount();
        // 记账1: 子商户Va-confirm
        ledgerUtil.confirmUpdate(subSum, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_CONFIRM, entity.getId(), factMemo, factAmount);
        // 记账2: 子商户Va+
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.ORIGIN_TYPE_CARD_WITHDRAW, LedgerConstant.FACT_CARD_WITHDRAW_IN, entity.getId(), factMemo, factAmount);
        // 预付费主卡提现: todo
    }

    // 释放商户担保金
    public void ledgeMfree(JMfreeEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity depVa = ledgerUtil.getDepositAccount(entity.getMerchantId(), entity.getCurrency());
        String factMemo = String.format("释放担保金:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户担保金-
        ledgerUtil.ledgeUpdate(depVa, LedgerConstant.ORIGIN_TYPE_MFREE, LedgerConstant.FACT_MFREE_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        // 记账2: 商户Va+
        ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_MFREE, LedgerConstant.FACT_MFREE_IN, entity.getId(), factMemo, entity.getAmount());
    }
}
