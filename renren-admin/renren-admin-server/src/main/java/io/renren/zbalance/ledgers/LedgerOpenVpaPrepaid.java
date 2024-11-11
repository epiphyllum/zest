package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
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
public class LedgerOpenVpaPrepaid {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private LedgerPrepaidOpenCharge ledgerPrepaidOpenCharge;

    // 共享子卡卡费
    public void ledgeOpenVpaPrepaidFreeze(JVpaJobEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getFeecurrency());
        String factMemo = null;
        factMemo = "冻结-批量预付费卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_VPA_OPEN_PREPAID, LedgerConstant.FACT_VPA_OPEN_PREPAID_FREEZE, entity.getId(), factMemo, factAmount);
        // 如果发行的是预付费子卡, 需要冻结预付费主卡总授权额度
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            ledgerPrepaidOpenCharge.ledgePrepaidOpenChargeFreeze(entity);
        }
    }

    // 解冻VPA子卡开通
    public void ledgeOpenVpaPrepaidUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getFeecurrency());
        String factMemo = "解冻-批量预付费卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_VPA_OPEN_PREPAID, LedgerConstant.FACT_VPA_OPEN_PREPAID_UN_FREEZE, entity.getId(), factMemo, factAmount);

        // 发行的预付费子卡
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            ledgerPrepaidOpenCharge.ledgePrepaidOpenChargeUnFreeze(entity);
        }
    }

    // 确认VPA子卡开通
    public void ledgeOpenVpaPrepaid(JVpaJobEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getFeecurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getFeecurrency());
        // 开卡费用
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-批量预防费卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();

        // 子商户va费用确认
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_VPA_OPEN_PREPAID, LedgerConstant.FACT_VPA_OPEN_PREPAID_CONFIRM, entity.getId(), factMemo, merchantFee);
        // 子商户开卡费用账户
        ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.ORIGIN_VPA_OPEN_PREPAID, LedgerConstant.FACT_VPA_OPEN_PREPAID_FEE_IN, entity.getId(), factMemo, merchantFee);
        // 预付费主卡额度
        ledgerPrepaidOpenCharge.ledgePrepaidOpenCharge(entity);
    }
}

