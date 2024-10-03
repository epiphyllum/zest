package io.renren.zbalance;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.*;
import io.renren.zin.config.CardProductConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class Ledger {
    @Resource
    private JBalanceDao jBalanceDao;
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

        String factMemo = "入金账户收到: " + entity.getAmount() + entity.getPayeraccountno() + ", 来账账户:" + entity.getPayeraccountno();
        BigDecimal factAmount = entity.getAmount();

        LambdaUpdateWrapper<JBalanceEntity> ledge = ledgerUtil.ledge(
                balanceEntity, LedgerConstant.IN_MONEY, entity.getId(), factMemo, factAmount);
        int update = jBalanceDao.update(ledge);
        if (update != 1) {
            throw new RenException("ledgeMoneyIn failed");
        }
    }

    // 原始凭证(200): 换汇
    public void ledgeExchange(JExchangeEntity entity) {
        Long merchantId = entity.getMerchantId();
        // 换汇出金账户
        JBalanceEntity outBalance = ledgerUtil.getInAccount(merchantId, entity.getPayerccy());
        if (outBalance == null) {
            log.error("出金账户{}不存在");
            throw new RenException("换汇错误: 出金账户不存在");
        }

        // 换汇入金账户
        JBalanceEntity inBalance = ledgerUtil.getInAccount(merchantId, entity.getPayeeccy());
        if (inBalance == null) {
            log.error("入金账户{}不存在");
            throw new RenException("换汇错误: 入金账户不存在");
        }

        // 出金账户
        {
            String factMemo = "换汇出金:" + entity.getAmount() + " " + entity.getPayerccy();
            BigDecimal factAmount = entity.getAmount().negate();

            LambdaUpdateWrapper<JBalanceEntity> ledge = ledgerUtil.ledge(outBalance, LedgerConstant.EXCHANGE_OUT, entity.getId(), factMemo, factAmount);
            int update = jBalanceDao.update(null, ledge);
            if (update != 1) {
                throw new RenException("ledgeExchange failed - out");
            }
        }

        // 入金账户
        {
            String factMemo = "换汇入金: " + entity.getSettleamount() + " " + entity.getPayeeccy();
            BigDecimal factAmount = entity.getStlamount();
            LambdaUpdateWrapper<JBalanceEntity> ledge = ledgerUtil.ledge(inBalance, LedgerConstant.EXCHANGE_IN, entity.getId(), factMemo, factAmount);
            int update = jBalanceDao.update(null, ledge);
            if (update != 1) {
                throw new RenException("ledgeExchange failed - in");
            }
        }

    }

    // 原始凭证(300): 商户入金D账户 ---> 商户VA账户
    public void ledgeI2v(JMerchantEntity merchant, JAllocateEntity entity) {
        JBalanceEntity inAccount = ledgerUtil.getInAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity depositAccount = ledgerUtil.getDepositAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity chargeFeeAccount = ledgerUtil.getChargeFeeAccount(merchant.getId(), entity.getCurrency());

        // 保证金缴纳比例 && 卡充值手续费比例
        BigDecimal depositRate = merchant.getDepositRate();
        BigDecimal chargeRate = merchant.getChargeRate();

        // 预收保证金， 预收充值手续费， 商户va实际到账
        BigDecimal depositAmount = entity.getAmount().multiply(depositRate).setScale(2, RoundingMode.UP);
        BigDecimal chargeFeeAmount = entity.getAmount().multiply(chargeRate).setScale(2, RoundingMode.UP);
        BigDecimal vaGet = entity.getAmount().subtract(depositAmount).subtract(chargeFeeAmount);

        // 记账1: 入金账户减(-)
        {
            BigDecimal factAmount = entity.getAmount().negate();
            String factMemo = String.format("VA充值%s, 入金账户扣除%s", entity.getAmount(), entity.getAmount());
            ledgerUtil.update(inAccount, LedgerConstant.I2V_OUT, entity.getId(), factMemo, factAmount);
        }

        // 记账2: 商户va账户(+)
        {
            BigDecimal factAmount = vaGet;
            String factMemo = "VA充值, VA账户到账:" + factAmount;
            ledgerUtil.update(vaAccount, LedgerConstant.I2V_IN, entity.getId(), factMemo, factAmount);
        }

        // 记账3: 保证预收账户(+)
        {
            BigDecimal factAmount = depositAmount;
            String factMemo = "VA充值, 保证金预收:" + factAmount;
            ledgerUtil.update(depositAccount, LedgerConstant.I2V_IN_DEPOSIT, entity.getId(), factMemo, factAmount);
        }

        // 记账4: 手续费预收账户(+)
        {
            String factMemo = "VA充值, 手续费预收:" + chargeFeeAccount;
            BigDecimal factAmount = chargeFeeAmount;
            ledgerUtil.update(chargeFeeAccount, LedgerConstant.I2v_IN_CHARGE_FEE, entity.getId(), factMemo, factAmount);
        }

    }

    // 商户va -> 入金账户
    public void ledgeV2i(JMerchantEntity merchant, JAllocateEntity entity) {
        JBalanceEntity inAccount = ledgerUtil.getInAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity depositAccount = ledgerUtil.getDepositAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity chargeFeeAccount = ledgerUtil.getChargeFeeAccount(merchant.getId(), entity.getCurrency());

        // 保证金缴纳比例 && 卡充值手续费比例
        BigDecimal depositRate = merchant.getDepositRate();
        BigDecimal chargeRate = merchant.getChargeRate();

        // 记账1: 入金账户(+)
        {
            BigDecimal factAmount = entity.getAmount();
            String factMemo = String.format("VA转入金:%s, 入金账户增加:%s", entity.getAmount(), entity.getAmount());
            ledgerUtil.update(inAccount, LedgerConstant.V2I_IN, entity.getId(), factMemo, factAmount);
        }

        // 记账2: 商户va账户(-)
        {
            String factMemo = "VA转入金, VA账户减少:" + entity.getAmount();
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.update(vaAccount, LedgerConstant.V2I_OUT, entity.getId(), factMemo, factAmount);
        }

        // 记账3: 保证预收账户
        {
            BigDecimal factAmount = entity.getAmount().negate();
            String factMemo = "VA转入金, 保证预收账户减少:" + factAmount.negate();
            ledgerUtil.update(depositAccount, LedgerConstant.V2I_OUT_DEPOSIT, entity.getId(), factMemo, factAmount);
        }
    }

    // 商户va ---> 子商户va
    public void ledgeM2s(JMerchantEntity merchant, JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(merchant.getId(), entity.getCurrency());
        // 记账1: 商户Va-
        {
            String factMemo = "商户va转子商户va:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.update(mVa, LedgerConstant.M2S_OUT, entity.getId(), factMemo, factAmount);
        }
        // 记账2: 子商户Va+
        {
            String factMemo = "商户va转子商户va:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.update(sVa, LedgerConstant.M2S_IN, entity.getId(), factMemo, factAmount);
        }

    }

    // 子商户va -->  商户va
    public void ledgeS2m(JMerchantEntity merchant, JAllocateEntity entity) {
        JBalanceEntity mVa = ledgerUtil.getVaAccount(merchant.getId(), entity.getCurrency());
        JBalanceEntity sVa = ledgerUtil.getSubVaAccount(merchant.getId(), entity.getCurrency());
        // 记账1: 商户Va+
        {
            String factMemo = "子商户va转商户va:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.update(mVa, LedgerConstant.S2M_IN, entity.getId(), factMemo, factAmount);
        }
        // 记账2: 子商户Va-
        {
            String factMemo = "子商户va转商户va:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.update(sVa, LedgerConstant.S2M_OUT, entity.getId(), factMemo, factAmount);
        }
    }

    // 原始凭证: 开卡费用
    public void ledgeOpenCard(JCardEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getCurrency());

        // 子商户va扣除费用
        {
            String factMemo = "扣减开卡费用:" + entity.getMerchantFee() + entity.getCurrency();
            BigDecimal factAmount = entity.getMerchantFee().negate();
            ledgerUtil.update(subVa, LedgerConstant.CARD_OPEN_FEE_OUT, entity.getId(), factMemo, factAmount);
        }

        // 子商户开卡费用账户
        {
            String factMemo = "开卡费用:" + entity.getMerchantFee() + entity.getCurrency();
            BigDecimal factAmount = entity.getMerchantFee();
            ledgerUtil.update(feeAccount, LedgerConstant.CARD_OPEN_FEE_IN, entity.getId(), factMemo, factAmount);
        }
    }

    public void ledgeOpenCardFail(JCardEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getCurrency());

        // 子商户va扣除费用
        {
            String factMemo = "开卡失败, 返还开卡费:" + entity.getMerchantFee() + entity.getCurrency();
            BigDecimal factAmount = entity.getMerchantFee();
            ledgerUtil.update(subVa, LedgerConstant.CARD_OPEN_FAIL_FEE_IN, entity.getId(), factMemo, factAmount);
        }

        // 子商户开卡费用账户
        {
            String factMemo = "开卡失败, 开卡费用退回:" + entity.getFee() + entity.getCurrency();
            BigDecimal factAmount = entity.getMerchantFee().negate();
            ledgerUtil.update(feeAccount, LedgerConstant.CARD_OPEN_FAIL_FEE_OUT, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡充值
    public void ledgeCardCharge(JMerchantEntity sub, JDepositEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity sSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());

        // 记账1: 子商户Va-
        {
            String factMemo = "卡充值扣减:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.update(subVa, LedgerConstant.CARD_CHARGE_OUT, entity.getId(), factMemo, factAmount);
        }

        // 记账2: 子商户sub_sum+
        {
            String factMemo = "卡充值:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.update(sSum, LedgerConstant.S2M_IN, entity.getId(), factMemo, factAmount);
        }
    }

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JMerchantEntity sub, JWithdrawEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());

        // 卡汇总充值资金账号
        JBalanceEntity subSum = ledgerUtil.getSubSumAccount(sub.getId(), entity.getCurrency());

        // 记账1: 子商户Va+
        {
            String factMemo = "卡资金退回:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount();
            ledgerUtil.update(subVa, LedgerConstant.CARD_WITHDRAW_IN, entity.getId(), factMemo, factAmount);
        }
        // 记账2: 子商户Va-
        {
            String factMemo = "卡资金退回:" + entity.getAmount() + entity.getCurrency();
            BigDecimal factAmount = entity.getAmount().negate();
            ledgerUtil.update(subSum, LedgerConstant.CARD_WITHDRAW_OUT, entity.getId(), factMemo, factAmount);
        }
    }


}
