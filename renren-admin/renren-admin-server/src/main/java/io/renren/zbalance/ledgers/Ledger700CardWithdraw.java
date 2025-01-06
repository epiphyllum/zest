package io.renren.zbalance.ledgers;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 卡提现
 */
@Service
@Slf4j
public class Ledger700CardWithdraw {
    // 卡提现
    public static final int ORIGIN_TYPE_CARD_WITHDRAW = 700;               // 卡提现
    public static final int FACT_CARD_WITHDRAW_IN_SUB_VA = 70001;          // 1. 子商户-VA+
    public static final int FACT_CARD_WITHDRAW_OUT_CARD_SUM = 70002;       // 2. 子商户-卡汇总资金账户(确认成功)
    public static final int FACT_CARD_WITHDRAW_OUT_CARD_CHARGE = 70003;    // 3. 子商户-退手续费
    public static final int FACT_CARD_WITHDRAW_OUT_AIP_CHARGE = 70004;     // 4. 通联-退手续费
    public static final int FACT_CARD_WITHDRAW_OUT_AIP_CARD_SUM = 70005;   // 5. 通联-卡汇总资金
    public static final int FACT_CARD_WITHDRAW_OUT_PREPAID_QUOTA = 70006;  // 6. 如果是预防费主卡: 预付费主卡可以余额减少

    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;

    // 卡资金提取: 将卡资金退回到子商户va
    public void ledgeCardWithdraw(JWithdrawEntity entity, JSubEntity sub) {
        BigDecimal showAmount = entity.getAmount().add(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "卡资金提取:" + showAmount;
        BigDecimal factAmount = entity.getAmount();

        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity cardSum = ledgerUtil.getCardSumAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity aipCardSum = ledgerUtil.getAipCardSumAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity charge = ledgerUtil.getChargeAccount(sub.getId(), entity.getCurrency());
        JBalanceEntity aipCharge = ledgerUtil.getAipChargeAccount(sub.getId(), entity.getCurrency());

        // 记账: 子商户-Va:  amount + abs(merchantfee)
        ledgerUtil.ledgeUpdate(subVa, ORIGIN_TYPE_CARD_WITHDRAW, FACT_CARD_WITHDRAW_IN_SUB_VA, entity.getId(), factMemo, factAmount.add(entity.getMerchantfee().negate()));

        // 记账: 子商户汇总-卡汇总资金
        ledgerUtil.ledgeUpdate(cardSum, ORIGIN_TYPE_CARD_WITHDRAW, FACT_CARD_WITHDRAW_OUT_CARD_SUM, entity.getId(), factMemo, factAmount.negate());
        // 记账: 子商户汇总-手续费 merchantfee已经是负数
        ledgerUtil.ledgeUpdate(charge, ORIGIN_TYPE_CARD_WITHDRAW, FACT_CARD_WITHDRAW_OUT_CARD_CHARGE, entity.getId(), factMemo, entity.getMerchantfee());

        // 记账: 通联汇总-手续费汇总  fee已经是负数
        ledgerUtil.ledgeUpdate(aipCharge, ORIGIN_TYPE_CARD_WITHDRAW, FACT_CARD_WITHDRAW_OUT_AIP_CHARGE, entity.getId(), factMemo, entity.getFee());
        // 记账: 通联汇总-卡充值
        ledgerUtil.ledgeUpdate(aipCardSum, ORIGIN_TYPE_CARD_WITHDRAW, FACT_CARD_WITHDRAW_OUT_AIP_CARD_SUM, entity.getId(), factMemo, entity.getAmount().negate());

        // 预付费主卡提现
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_PREPAID)) {
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            JBalanceEntity ppMain = ledgerUtil.getPrepaidQuotaAccount(cardEntity.getId(), cardEntity.getCurrency());
            ledgerUtil.ledgeUpdate(ppMain, ORIGIN_TYPE_CARD_WITHDRAW, FACT_CARD_WITHDRAW_OUT_PREPAID_QUOTA, entity.getId(), factMemo, factAmount.negate());
        }
        // 钱包主卡提现: not permitted
        else if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_MAIN_WALLET)) {
            log.error("钱包主卡不允许提现");
            throw new RenException("not permitted");
        }

    }
}
