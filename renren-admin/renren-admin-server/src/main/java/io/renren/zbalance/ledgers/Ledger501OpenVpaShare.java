package io.renren.zbalance.ledgers;

import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class Ledger501OpenVpaShare {
    // vpa共享子卡开卡
    public static final int ORIGIN_VPA_SHARE_OPEN = 501;                      // vpa共享子卡开卡
    public static final int FACT_VPA_SHARE_OPEN_FREEZE_SUB_VA = 50100;        // 1. 子商户va冻结
    public static final int FACT_VPA_SHARE_OPEN_UNFREEZE_SUB_VA = 50101;      // 2. 子商户va解冻
    public static final int FACT_VPA_SHARE_OPEN_CONFIRM_SUB_VA = 50102;       // 3. 子商户va确认
    public static final int FACT_VPA_SHARE_OPEN_IN_CARD_FEE = 50103;          // 4. 子商户费用账户+
    public static final int FACT_VPA_SHARE_OPEN_IN_AIP_CARD_FEE = 50104;      // 5. 通联开卡费 - 0

    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private Ledger601PrepaidOpenCharge ledger601PrepaidOpenCharge;

    // 共享子卡卡费
    public void ledgeOpenVpaShareFreeze(JVpaJobEntity entity) {
        log.info("ledgeOpenVpaShareFreeze: {}", entity);
        // 子商户va扣除费用冻结
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "冻结-批量共享子卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.freezeUpdate(subVa, ORIGIN_VPA_SHARE_OPEN, FACT_VPA_SHARE_OPEN_FREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 解冻VPA子卡开通
    public void ledgeOpenVpaShareUnFreeze(JVpaJobEntity entity) {
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        String factMemo = "解冻-批量共享子卡费用:" + BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal factAmount = entity.getMerchantfee();
        ledgerUtil.unFreezeUpdate(subVa, ORIGIN_VPA_SHARE_OPEN, FACT_VPA_SHARE_OPEN_UNFREEZE_SUB_VA, entity.getId(), factMemo, factAmount);
    }

    // 确认VPA子卡开通
    public void ledgeOpenVpaShare(JVpaJobEntity entity) {
        // 开卡费用账户
        BigDecimal showMerchantFee = BigDecimal.ZERO.add(entity.getMerchantfee()).setScale(2, RoundingMode.HALF_UP);
        String factMemo = "确认-批量共享子卡费用:" + showMerchantFee;
        BigDecimal merchantFee = entity.getMerchantfee();

        // 子商户va-扣除费用
        JBalanceEntity subVa = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.confirmUpdate(subVa, ORIGIN_VPA_SHARE_OPEN, FACT_VPA_SHARE_OPEN_CONFIRM_SUB_VA, entity.getId(), factMemo, merchantFee);

        // 子商户-开卡费用账户
        JBalanceEntity cardFee = ledgerUtil.getCardFeeAccount(entity.getSubId(), entity.getProductcurrency());
        ledgerUtil.ledgeUpdate(cardFee, ORIGIN_VPA_SHARE_OPEN, FACT_VPA_SHARE_OPEN_IN_CARD_FEE, entity.getId(), factMemo, merchantFee);

        // 子商户发卡数量增加
        JBalanceEntity cardCount = ledgerUtil.getCardCountAccount(entity.getSubId(), entity.getCurrency());
        ledgerUtil.ledgeUpdate(cardCount, Ledger500OpenCard.ORIGIN_CARD_OPEN,
                Ledger500OpenCard.FACT_CARD_OPEN_IN_CARD_COUNT, entity.getId(), "开卡1张", new BigDecimal(entity.getNum()));

    }
}

