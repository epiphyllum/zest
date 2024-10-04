package io.renren.manager;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JAllocateDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Service
public class JAllocateManager {

    @Resource
    public JMerchantDao jMerchantDao;
    @Resource
    public JAllocateDao jAllocateDao;
    @Resource
    public TransactionTemplate tx;
    @Resource
    public Ledger ledger;
    @Resource
    public LedgerUtil ledgerUtil;

    // 入金账户转va
    public void handleI2v(JAllocateEntity entity, JMerchantEntity merchant) {
        JBalanceEntity inAccount = ledgerUtil.getInAccount(entity.getMerchantId(), entity.getCurrency());
        if (inAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("入金账户余额不足, 入金账户:" + inAccount.getBalance());
        }
        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledger.ledgeI2v(entity, merchant);
            JBalanceEntity after = ledgerUtil.getInAccount(entity.getMerchantId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("入金账户余额不足");
            }
        });
    }

    // va转入金账户
    public void handleV2i(JAllocateEntity entity, JMerchantEntity merchant) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        if (vaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("VA账户余额不足, VA账户:" + vaAccount.getBalance());
        }
        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledger.ledgeV2i(entity, merchant);
            JBalanceEntity after = ledgerUtil.getSubVaAccount(entity.getMerchantId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("商户VA账户余额不足");
            }
        });
    }

    // 商户VA转子商户VA
    public void handleM2s(JAllocateEntity entity) {
        JBalanceEntity vaAccount = ledgerUtil.getVaAccount(entity.getMerchantId(), entity.getCurrency());
        if (vaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("VA账户余额不足, VA账户:" + vaAccount.getBalance());
        }
        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledger.ledgeM2s(entity);
            JBalanceEntity after = ledgerUtil.getSubVaAccount(entity.getMerchantId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("子商户VA账户余额不足");
            }
        });
    }

    // 子商户VA转商户VA
    public void handleS2m(JAllocateEntity entity) {

        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
        if (subVaAccount.getBalance().compareTo(entity.getAmount()) < 0) {
            throw new RenException("子商户VA账户余额不足, VA账户:" + subVaAccount.getBalance());
        }

        tx.executeWithoutResult(status -> {
            jAllocateDao.insert(entity);
            ledger.ledgeS2m(entity);

            JBalanceEntity after = ledgerUtil.getSubVaAccount(entity.getSubId(), entity.getCurrency());
            if (after.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RenException("子商户VA账户余额不足");
            }

        });
    }

    // 资金调度处理
    public void handleAllocation(JAllocateEntity entity, JMerchantEntity merchant) {
        switch (entity.getType()) {
            case "m2s":
                handleM2s(entity);
                break;
            case "s2m":
                handleS2m(entity);
                break;
            case "i2v":
                handleI2v(entity, merchant);
                break;
            case "v2i":
                handleV2i(entity, merchant);
                break;
        }
    }
}


