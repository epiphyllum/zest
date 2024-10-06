package io.renren.zbalance;

import io.renren.commons.tools.exception.RenException;
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
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(entity.getMerchantId());

        String currency = entity.getCurrency();

        // 拿到入金账户的余额记录
        JBalanceEntity balanceEntity = ledgerUtil.getInAccount(jMerchantEntity.getId(), currency);

        String factMemo = String.format("入金账户收到: %s, 来账账户:%s, 要求账户:%s", entity.getAmount().setScale(2, RoundingMode.HALF_UP), entity.getPayeraccountno(), entity.getPayeraccountno());
        BigDecimal factAmount = entity.getAmount();
        ledgerUtil.ledgeUpdate(balanceEntity, LedgerConstant.IN_MONEY, entity.getId(), factMemo, factAmount);
    }

    // 换汇冻结
    public void ledgeExchangeFreeze(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        JBalanceEntity outBalance = ledgerUtil.getInAccount(merchantId, entity.getPayerccy());
        String factMemo = String.format("换汇冻结: %s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        ledgerUtil.freezeUpdate(outBalance, LedgerConstant.EXCHANGE_FREEZE, entity.getId(), factMemo, entity.getAmount());
    }

    // 换汇解冻
    public void ledgeExchangeUnFreeze(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        JBalanceEntity outBalance = ledgerUtil.getInAccount(merchantId, entity.getPayerccy());
        String factMemo = String.format("换汇解冻: %s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        ledgerUtil.unFreezeUpdate(outBalance, LedgerConstant.EXCHANGE_UN_FREEZE, entity.getId(), factMemo, entity.getAmount());
    }

    // 原始凭证(200): 换汇
    public void ledgeExchange(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        // 换汇出金账户
        JBalanceEntity outBalance = ledgerUtil.getInAccount(merchantId, entity.getPayerccy());
        if (outBalance == null) {
            log.error("出金账户[{}][{}]不存在", entity.getMerchantName(), entity.getPayerccy());
            throw new RenException("换汇错误: 出金账户不存在");
        }

        // 换汇入金账户
        JBalanceEntity inBalance = ledgerUtil.getInAccount(merchantId, entity.getPayeeccy());
        if (inBalance == null) {
            log.error("入金账户[{}][{}]不存在", entity.getMerchantName(), entity.getPayeeccy());
            throw new RenException("换汇错误: 入金账户不存在");
        }

        // 判断换汇出金账户余额是否足够
        if (outBalance.getBalance().compareTo(inBalance.getBalance()) < 0) {
            throw new RenException("换汇出金账户余额不足");
        }

        // 出金账户
        {
            String factMemo = String.format("换汇出金确认:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.confirmUpdate(outBalance, LedgerConstant.EXCHANGE_CONFIRM, entity.getId(), factMemo, factAmount);
        }

        // 入金账户
        {
            String factMemo = String.format("换汇入金:%s", BigDecimal.ZERO.add(entity.getStlamount()).setScale(2, RoundingMode.HALF_UP));
            BigDecimal factAmount = entity.getStlamount();
            ledgerUtil.ledgeUpdate(inBalance, LedgerConstant.EXCHANGE_IN, entity.getId(), factMemo, factAmount);
        }
    }


    // 原始凭证(300): 商户入金D账户 ---> 商户VA账户
    public void ledgeI2v(JAllocateEntity entity, JMerchantEntity merchant) {

        // 入金账户, 商户va账户, 保证金预收账户, 手续费预收账户
        JBalanceEntity inAccount = ledgerUtil.getInAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity depositAccount = ledgerUtil.getDepositAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity chargeFeeAccount = ledgerUtil.getChargeFeeAccount(entity.getMerchantId(), entity.getCurrency());

        // 保证金缴纳比例 && 卡充值手续费比例
        BigDecimal depositRate = merchant.getDepositRate();
        BigDecimal chargeRate = merchant.getChargeRate();

        // 预收保证金金额， 预收手续费进程, 商户VA到账金额
        BigDecimal depositAmount = entity.getAmount().multiply(depositRate).setScale(2, RoundingMode.UP);
        BigDecimal chargeFeeAmount = entity.getAmount().multiply(chargeRate).setScale(2, RoundingMode.UP);
        BigDecimal vaGet = entity.getAmount().subtract(depositAmount).subtract(chargeFeeAmount);

        //
        BigDecimal showAmount = BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);

        // 记账1: 入金账户减(-)
        {
            BigDecimal factAmount = entity.getAmount().negate();
            String factMemo = String.format("VA充值:%s, 入金账户扣除:%s", showAmount, showAmount);
            ledgerUtil.ledgeUpdate(inAccount, LedgerConstant.I2V_OUT, entity.getId(), factMemo, factAmount);
        }

        // 记账2: 保证预收账户(+)
        {
            BigDecimal factAmount = depositAmount;
            String factMemo = String.format("VA充值:%s, 保证金预收:%s", showAmount, BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP));
            ledgerUtil.ledgeUpdate(depositAccount, LedgerConstant.I2V_IN_DEPOSIT, entity.getId(), factMemo, factAmount);
        }

        // 记账3: 商户va账户(+)
        {
            BigDecimal factAmount = vaGet;
            String factMemo = String.format("VA充值:%s, VA到账:%s", showAmount, BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP));
            ledgerUtil.ledgeUpdate(vaAccount, LedgerConstant.I2V_IN, entity.getId(), factMemo, factAmount);
        }

        // 记账4: 手续费预收账户(+)
        {
            BigDecimal factAmount = chargeFeeAmount;
            String factMemo = String.format("VA充值:%s, 手续费预收:%s", showAmount, BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP));
            ledgerUtil.ledgeUpdate(chargeFeeAccount, LedgerConstant.I2v_IN_CHARGE_FEE, entity.getId(), factMemo, factAmount);
        }

    }

    // 商户va -> 入金账户
    public void ledgeV2i(JAllocateEntity entity, JMerchantEntity merchant) {
        JBalanceEntity inAccount = ledgerUtil.getInAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity depositAccount = ledgerUtil.getDepositAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity chargeFeeAccount = ledgerUtil.getChargeFeeAccount(merchant.getId(), entity.getCurrency());

        // 保证金缴纳比例 && 卡充值手续费比例
        BigDecimal chargeRate = merchant.getChargeRate();     // 1%
        BigDecimal depositRate = merchant.getDepositRate();   // 5%
        BigDecimal vaRate = BigDecimal.ONE.subtract(chargeRate).subtract(depositRate);  // 94%

        // 计算退还手续费
        BigDecimal refundFee = chargeRate.multiply(entity.getAmount()).divide(vaRate);

        // 展示金额
        BigDecimal showAmount = BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal showRefundFee = BigDecimal.ZERO.add(refundFee).setScale(2, RoundingMode.HALF_UP);
        // 记账1: 商户va账户(-)
        {
            BigDecimal factAmount = entity.getAmount().negate();
            String factMemo = String.format("VA转入金:%s, VA账户减少:%s", showAmount, showAmount);
            ledgerUtil.ledgeUpdate(vaAccount, LedgerConstant.V2I_OUT, entity.getId(), factMemo, factAmount);
        }

        // 记账2: 入金账户(+) 本金
        {
            BigDecimal factAmount = entity.getAmount();
            String factMemo = String.format("VA转入金:%s, 入金账户增加本金:%s", showAmount, showAmount);
            ledgerUtil.ledgeUpdate(inAccount, LedgerConstant.V2I_IN, entity.getId(), factMemo, factAmount);
        }

        // 记账3: 入金账户(+) 退手续费
        {
            BigDecimal factAmount = entity.getAmount();
            String factMemo = String.format("VA转入金:%s, 入金账户增加退回手续费:%s", showAmount, showRefundFee);
            ledgerUtil.ledgeUpdate(inAccount, LedgerConstant.V2I_IN, entity.getId(), factMemo, factAmount);
        }

        // 记账4: 预收手续费(-)
        {
            BigDecimal factAmount = refundFee;
            String factMemo = String.format("VA转入金:%s, 手续费预收账户减少:%s", showAmount, showRefundFee);
            ledgerUtil.ledgeUpdate(depositAccount, LedgerConstant.V2I_OUT_CHARGE_FEE, entity.getId(), factMemo, factAmount);
        }

    }

    // 商户va ---> 子商户va
    public void ledgeM2s(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("商户va转子商户va:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));

        // 记账1: 商户Va-
        {
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.ledgeUpdate(mVa, LedgerConstant.M2S_OUT, entity.getId(), factMemo, factAmount);
        }
        // 记账2: 子商户Va+
        {
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.ledgeUpdate(sVa, LedgerConstant.M2S_IN, entity.getId(), factMemo, factAmount);
        }
    }

    // 子商户va -->  商户va
    public void ledgeS2m(JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = String.format("子商户VA转商户VA:%s", BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP));
        // 记账1: 商户Va+
        {
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.ledgeUpdate(mVa, LedgerConstant.S2M_IN, entity.getId(), factMemo, factAmount);
        }
        // 记账2: 子商户Va-
        {
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.ledgeUpdate(sVa, LedgerConstant.S2M_OUT, entity.getId(), factMemo, factAmount);
        }
    }

    // 开卡冻结
    public void ledgeOpenCardFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = "开卡费用冻结:" + BigDecimal.ZERO.add(entity.getMerchantFee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantFee();
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.CARD_OPEN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡开解冻
    public void ledgeOpenCardUnFreeze(JCardEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        String factMemo = "开卡费用解冻:" + BigDecimal.ZERO.add(entity.getMerchantFee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantFee();
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.CARD_OPEN_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 原始凭证: 开卡费用
    public void ledgeOpenCard(JCardEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getCurrency());

        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantFee()).setScale(2, RoundingMode.HALF_UP);

        // 子商户va扣除费用
        {
            String factMemo = "开卡费用确认:" + showMerchantFee;
            BigDecimal factAmount = entity.getMerchantFee().negate();
            ledgerUtil.ledgeUpdate(subVa, LedgerConstant.CARD_OPEN_CONFIRM, entity.getId(), factMemo, factAmount);
        }

        // 子商户开卡费用账户
        {
            String factMemo = "开卡费用:" + showMerchantFee;
            BigDecimal factAmount = entity.getMerchantFee();
            ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.CARD_OPEN_FEE_IN, entity.getId(), factMemo, factAmount);
        }
    }

    //
    public void ledgeCardChargeFreeze(JDepositEntity entity, JSubEntity sub) {
        String factMemo = "卡充值冻结:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();

        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.CARD_CHARGE_FREEZE, entity.getId(), factMemo, factAmount);
    }

    //
    public void ledgeCardChargeUnFreeze(JDepositEntity entity, JSubEntity sub) {
        String factMemo = "卡充值解冻:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(subVa, LedgerConstant.CARD_CHARGE_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡充值
    public void ledgeCardCharge(JDepositEntity entity, JMerchantEntity sub) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal showAmount = BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        // 记账1: 子商户Va-
        {
            String factMemo = "卡充值扣减确认:" + showAmount;
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.ledgeUpdate(subVa, LedgerConstant.CARD_CHARGE_CONFIRM, entity.getId(), factMemo, factAmount);
        }
        // 记账2: 子商户sub_sum+
        {
            String factMemo = "卡充值:" + showAmount;
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.ledgeUpdate(subSum, LedgerConstant.S2M_IN, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡资金退回: 冻结
    public void ledgeCardWithdrawFreeze(JMerchantEntity sub, JWithdrawEntity entity) {
        String factMemo = "卡资金退回冻结:" + BigDecimal.ZERO.add(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        ledgerUtil.freezeUpdate(subSum, LedgerConstant.CARD_WITHDRAW_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡资金退回: 解冻
    public void ledgeCardWithdrawUnFreeze(JMerchantEntity sub, JWithdrawEntity entity) {
        BigDecimal factAmount = entity.getAmount();
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        String factMemo = "卡资金退回确认:" + BigDecimal.ZERO.add(factAmount).setScale(2, RoundingMode.HALF_UP);
        ledgerUtil.unFreezeUpdate(subSum, LedgerConstant.CARD_WITHDRAW_UN_FREEZE, entity.getId(), factMemo, factAmount);
    }

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JMerchantEntity sub, JWithdrawEntity entity) {
        // 子商户va, 卡汇总充值资金账号
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        // 记账2: 子商户Va-confirm
        {
            String factMemo = "卡资金退回确认:" + showAmount;
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.confirmUpdate(subSum, LedgerConstant.CARD_WITHDRAW_CONFIRM, entity.getId(), factMemo, factAmount);
        }

        // 记账1: 子商户Va+
        {
            String factMemo = "卡资金退回:" + showAmount;
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.ledgeUpdate(subVa, LedgerConstant.CARD_WITHDRAW_IN, entity.getId(), factMemo, factAmount);
        }
    }
}
