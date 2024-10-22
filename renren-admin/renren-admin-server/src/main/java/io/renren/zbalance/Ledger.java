package io.renren.zbalance;

import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class Ledger {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;

    // 原始凭证(100):  收到商户入金
    public void ledgeMoneyIn(JMoneyEntity entity) {
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        String currency = entity.getCurrency();

        // 商户va账户, 保证金预收账户, 手续费预收账户
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity depositAccount = ledgerUtil.getDepositAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity chargeFeeAccount = ledgerUtil.getChargeFeeAccount(entity.getMerchantId(), entity.getCurrency());

        // 保证金缴纳比例 && 卡充值手续费比例
        BigDecimal depositRate = merchant.getDepositRate();
        BigDecimal chargeRate = merchant.getChargeRate();

        // 预收保证金金额， 预收手续费进程, 商户VA到账金额
        BigDecimal depositAmount = entity.getAmount().multiply(depositRate).setScale(6, RoundingMode.UP);
        BigDecimal chargeFeeAmount = entity.getAmount().multiply(chargeRate).setScale(6, RoundingMode.UP);
        BigDecimal vaGet = entity.getAmount().subtract(depositAmount).subtract(chargeFeeAmount);

        // 记账描述
        BigDecimal showAmount = BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal showFee = BigDecimal.ZERO.add(chargeFeeAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal showDeposit = BigDecimal.ZERO.add(depositAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal showVa = BigDecimal.ZERO.add(vaGet).setScale(2, RoundingMode.HALF_UP);
        String factMemo = String.format("入金:%s, VA到账:%s, 充值手续费:%s, 保证金:%s", showAmount, showVa, showFee, showDeposit);

        // 记账4: 保证预收账户(+)
        ledgerUtil.ledgeUpdate(depositAccount, LedgerConstant.ORIGIN_TYPE_MONEY, LedgerConstant.FACT_MONEY_DEPOSIT, entity.getId(), factMemo, depositAmount);
        // 记账5: 商户va账户(+)
        ledgerUtil.ledgeUpdate(vaAccount, LedgerConstant.ORIGIN_TYPE_MONEY, LedgerConstant.FACT_MONEY_VA, entity.getId(), factMemo, vaGet);
        // 记账6: 手续费预收账户(+)
        ledgerUtil.ledgeUpdate(chargeFeeAccount, LedgerConstant.ORIGIN_TYPE_MONEY, LedgerConstant.FACT_MONEY_CHARGE_FEE, entity.getId(), factMemo, chargeFeeAmount);
    }

    // 换汇冻结
    public void ledgeExchangeFreeze(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();

        // 更具商户费率算出 入金分配份额
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal chargeShare = merchant.getChargeRate().multiply(hundred);
        BigDecimal depositShare = merchant.getDepositRate().multiply(hundred);
        BigDecimal vaShare = hundred.subtract(chargeShare).subtract(depositShare);

        // 先反算出入金总额
        BigDecimal total = hundred.multiply(entity.getAmount()).divide(vaShare, 2, RoundingMode.HALF_UP);

        // 再算出这个金额对应多少保证金和手续费
        BigDecimal vaAmount = entity.getAmount();
        BigDecimal depositAmount = total.multiply(merchant.getDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal feeAmount = total.subtract(depositAmount).subtract(vaAmount);

        log.debug("reverse in:     {} = (100 * {}) / {}", total, entity.getAmount(), vaShare);
        log.debug("reverse deposit:{} = {} * {}", depositAmount, total, merchant.getDepositRate());
        log.debug("reverse fee:    {} = {} - {} - {}", feeAmount, total, depositAmount, vaAmount);

        // 凭证说明
        BigDecimal showVa = BigDecimal.ZERO.add(vaAmount).setScale(2, RoundingMode.HALF_UP);
        String factMemo = String.format("冻结 | 换汇卖出%s买入%s | 卖出金额: %s, %s账户: (VA:%s, 保证金:%s, 充值费用:%s)",
                entity.getPayerccy(), entity.getPayeeccy(),
                entity.getAmount(),

                entity.getPayeeccy(),
                showVa,
                BigDecimal.ZERO.add(depositAmount).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.add(feeAmount).setScale(2, RoundingMode.HALF_UP)
        );

        // 需要冻结的三个账号
        JBalanceEntity outBalance = ledgerUtil.getVaAccount(merchantId, entity.getPayerccy());
        JBalanceEntity depBalance = ledgerUtil.getDepositAccount(merchantId, entity.getPayerccy());
        JBalanceEntity feeBalance = ledgerUtil.getChargeFeeAccount(merchantId, entity.getPayerccy());
        ledgerUtil.freezeUpdate(outBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_FREEZE_VA, entity.getId(), factMemo, vaAmount);
        ledgerUtil.freezeUpdate(depBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_FREEZE_DEPOSIT, entity.getId(), factMemo, depositAmount);
        ledgerUtil.freezeUpdate(feeBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_FREEZE_CHARGE_FEE, entity.getId(), factMemo, feeAmount);
    }

    // 换汇解冻
    public void ledgeExchangeUnFreeze(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();


        // 更具商户费率算出 入金分配份额
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal chargeShare = merchant.getChargeRate().multiply(hundred);
        BigDecimal depositShare = merchant.getDepositRate().multiply(hundred);
        BigDecimal vaShare = hundred.subtract(chargeShare).subtract(depositShare);

        // 先反算出入金总额
        BigDecimal total = hundred.multiply(entity.getAmount()).divide(vaShare, 2, RoundingMode.HALF_UP);

        // 再算出这个金额对应多少保证金和手续费
        BigDecimal vaAmount = entity.getAmount();
        BigDecimal depositAmount = total.multiply(merchant.getDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal feeAmount = total.subtract(depositAmount).subtract(vaAmount);

        log.debug("reverse in:     {} = (100 * {}) / {}", total, entity.getAmount(), vaShare);
        log.debug("reverse deposit:{} = {} * {}", depositAmount, total, merchant.getDepositRate());
        log.debug("reverse fee:    {} = {} - {} - {}", feeAmount, total, depositAmount, vaAmount);

        // 凭证说明
        BigDecimal showVa = BigDecimal.ZERO.add(vaAmount).setScale(2, RoundingMode.HALF_UP);
        String factMemo = String.format("解冻 | 换汇卖出%s买入%s | 卖出金额:%s, (VA:%s, 保证金:%s, 充值费用:%s)",
                entity.getPayerccy(), entity.getPayeeccy(),

                entity.getPayeeccy(),
                entity.getAmount(),
                showVa,
                BigDecimal.ZERO.add(depositAmount).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.add(feeAmount).setScale(2, RoundingMode.HALF_UP)
        );

        // 需要解冻的三个账号
        JBalanceEntity outBalance = ledgerUtil.getVaAccount(merchantId, entity.getPayerccy());
        JBalanceEntity depBalance = ledgerUtil.getDepositAccount(merchantId, entity.getPayerccy());
        JBalanceEntity feeBalance = ledgerUtil.getChargeFeeAccount(merchantId, entity.getPayerccy());
        ledgerUtil.unFreezeUpdate(outBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_UN_FREEZE_VA, entity.getId(), factMemo, vaAmount);
        ledgerUtil.unFreezeUpdate(depBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_UN_FREEZE_DEPOSIT, entity.getId(), factMemo, depositAmount);
        ledgerUtil.unFreezeUpdate(feeBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_UN_FREEZE_CHARGE_FEE, entity.getId(), factMemo, feeAmount);
    }

    // 原始凭证: 换汇
    public void ledgeExchange(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();

        // 更具商户费率算出 入金分配份额
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal chargeShare = merchant.getChargeRate().multiply(hundred);
        BigDecimal depositShare = merchant.getDepositRate().multiply(hundred);
        BigDecimal vaShare = hundred.subtract(chargeShare).subtract(depositShare);

        // 先反算出入金总额
        BigDecimal total = hundred.multiply(entity.getAmount()).divide(vaShare, 2, RoundingMode.HALF_UP);

        // 再算出这个金额对应多少保证金和手续费
        BigDecimal vaAmount = entity.getAmount();
        BigDecimal depositAmount = total.multiply(merchant.getDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal feeAmount = total.subtract(depositAmount).subtract(vaAmount);

        log.debug("reverse in:     {} = (100 * {}) / {}", total, entity.getAmount(), vaShare);
        log.debug("reverse deposit:{} = {} * {}", depositAmount, total, merchant.getDepositRate());
        log.debug("reverse fee:    {} = {} - {} - {}", feeAmount, total, depositAmount, vaAmount);


        // 反算实际目标账户的入金
        BigDecimal targetTotal = hundred.multiply(entity.getStlamount()).divide(vaShare, 2, RoundingMode.HALF_UP);


        // 再计算买入币种对应的金额
        BigDecimal targetDepositAmount = targetTotal.multiply(merchant.getDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal targetFeeAmount = targetTotal.multiply(merchant.getChargeRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal targetVaAmount = targetTotal.subtract(targetDepositAmount).subtract(targetFeeAmount);

        log.debug("reverse target in:     {} = (100 * {}) / {}", targetTotal, entity.getStlamount(), vaShare);
        log.debug("reverse target deposit:{} = {} * {}", targetDepositAmount, targetTotal, merchant.getDepositRate());
        log.debug("reverse target fee:    {} = {} - {} - {}", targetFeeAmount, targetTotal, depositAmount, vaAmount);

        // 凭证说明
        BigDecimal showVa = BigDecimal.ZERO.add(vaAmount).setScale(2, RoundingMode.HALF_UP);
        String factMemo = String.format("确认 | 换汇卖出%s买入%s | 卖出金额:%s, (VA:%s, 保证金:%s, 充值费用:%s) | 买入%s: 换汇:%s, 换算:(VA:%s, 保证金:%s, 充值费用:%s)",
                entity.getPayerccy(), entity.getPayeeccy(),
                // 卖出
                entity.getAmount(),
                showVa,
                BigDecimal.ZERO.add(depositAmount).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.add(feeAmount).setScale(2, RoundingMode.HALF_UP),
                // 买入
                entity.getPayeeccy(),
                entity.getStlamount(),
                // 账户
                targetVaAmount,
                targetDepositAmount,
                targetFeeAmount
        );

        // 卖出币种: 确认冻结
        JBalanceEntity outBalance = ledgerUtil.getVaAccount(merchantId, entity.getPayerccy());
        JBalanceEntity depBalance = ledgerUtil.getDepositAccount(merchantId, entity.getPayerccy());
        JBalanceEntity feeBalance = ledgerUtil.getChargeFeeAccount(merchantId, entity.getPayerccy());
        ledgerUtil.confirmUpdate(outBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_CONFIRM_VA, entity.getId(), factMemo, vaAmount);
        ledgerUtil.confirmUpdate(depBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_CONFIRM_DEPOSIT, entity.getId(), factMemo, depositAmount);
        ledgerUtil.confirmUpdate(feeBalance, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_CONFIRM_CHARGE_FEE, entity.getId(), factMemo, feeAmount);

        // 买入币种: 入账
        JBalanceEntity targetVa = ledgerUtil.getVaAccount(merchantId, entity.getPayeeccy());
        JBalanceEntity targetFee = ledgerUtil.getChargeFeeAccount(merchantId, entity.getPayeeccy());
        JBalanceEntity targetDep = ledgerUtil.getDepositAccount(merchantId, entity.getPayeeccy());
        ledgerUtil.ledgeUpdate(targetVa, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_IN_VA, entity.getId(), factMemo, targetVaAmount);
        ledgerUtil.ledgeUpdate(targetDep, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_IN_DEPOSIT, entity.getId(), factMemo, targetDepositAmount);
        ledgerUtil.ledgeUpdate(targetFee, LedgerConstant.ORIGIN_TYPE_EXCHANGE, LedgerConstant.FACT_EXCHANGE_IN_CHARGE_FEE, entity.getId(), factMemo, targetFeeAmount);
    }

    // 商户va ---> 子商户va
    public void ledgeM2s(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("转入子商户:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户Va-
        ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_M2S, LedgerConstant.FACT_M2S_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        ledgerUtil.ledgeUpdate(sVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_M2S, LedgerConstant.FACT_M2S_IN, entity.getId(), factMemo, entity.getAmount());
    }

    // 子商户va -->  商户va
    public void ledgeS2m(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("转出子商户:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 子商户Va-
        ledgerUtil.ledgeUpdate(sVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_S2M, LedgerConstant.FACT_S2M_OUT, entity.getId(), factMemo, entity.getAmount().negate());
        // 记账2: 商户Va+
        ledgerUtil.ledgeUpdate(mVa, LedgerConstant.ORIGIN_TYPE_ALLOCATE_S2M, LedgerConstant.FACT_S2M_IN, entity.getId(), factMemo, entity.getAmount());
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
