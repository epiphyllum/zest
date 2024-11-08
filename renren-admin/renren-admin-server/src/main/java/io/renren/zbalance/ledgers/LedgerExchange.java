package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zbalance.LedgerConstant;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LedgerExchange {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

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

}