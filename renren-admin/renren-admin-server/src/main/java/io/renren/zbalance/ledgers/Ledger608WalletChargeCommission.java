package io.renren.zbalance.ledgers;

import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

// 充值佣金
@Service
public class Ledger608WalletChargeCommission {
    @Resource
    private LedgerUtil ledgerUtil;
}
