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
public class LedgerMoneyIn {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 原始凭证(100):  收到商户入金
    public void ledgeMoneyIn(JMoneyEntity entity) {
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());

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
}
