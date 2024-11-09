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
public class LedgerOpenVpa {
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private LedgerPrepaidOpenCharge ledgerPrepaidOpenCharge;

    // VPA子卡开卡费
    public void ledgeOpenVpaFreeze(JVpaJobEntity entity) {
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getFeecurrency());
        String factMemo = "冻结-共享子卡开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.freezeUpdate(subVa, LedgerConstant.ORIGIN_VPA_OPEN, LedgerConstant.FACT_VPA_OPEN_FREEZE, entity.getId(), factMemo, factAmount);

        // 如果发行的是预付费子卡, 需要冻结预付费主卡总授权额度
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            ledgerPrepaidOpenCharge.ledgePrepaidOpenChargeFreeze(entity);
        }
    }

    // 解冻VPA子卡开通
    public void ledgeOpenVpaUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getFeecurrency());
        String factMemo = "解冻-共享子卡开卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, LedgerConstant.ORIGIN_VPA_OPEN, LedgerConstant.FACT_VPA_OPEN_UN_FREEZE, entity.getId(), factMemo, factAmount);

        // 发行的预付费子卡
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            ledgerPrepaidOpenCharge.ledgePrepaidOpenChargeUnFreeze(entity);
        }
    }

    // 确认VPA子卡开通
    public void ledgeOpenVpa(JVpaJobEntity entity) {
        // 子商户va
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getFeecurrency());
        // 开卡费用账户
        JBalanceEntity feeAccount = ledgerUtil.getSubFeeAccount(entity.getSubId(), entity.getFeecurrency());
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-共享子卡开卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();
        // 子商户va扣除费用
        ledgerUtil.confirmUpdate(subVa, LedgerConstant.ORIGIN_VPA_OPEN, LedgerConstant.FACT_VPA_OPEN_CONFIRM, entity.getId(), factMemo, merchantFee);
        // 子商户开卡费用账户
        ledgerUtil.ledgeUpdate(feeAccount, LedgerConstant.ORIGIN_VPA_OPEN, LedgerConstant.FACT_VPA_OPEN_FEE_IN, entity.getId(), factMemo, merchantFee);
        // 发行的预付费子卡
        if (entity.getMarketproduct().equals(ZinConstant.MP_VPA_PREPAID)) {
            ledgerPrepaidOpenCharge.ledgePrepaidOpenCharge(entity);
        }
    }
}

