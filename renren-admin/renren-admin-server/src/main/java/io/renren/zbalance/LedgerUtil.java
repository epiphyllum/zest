package io.renren.zbalance;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JLogDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JLogEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zcommon.ZestConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class LedgerUtil {
    @Resource
    private JLogDao jLogDao;
    @Resource
    private JBalanceDao jBalanceDao;

    /**
     * 创建账户
     */
    public void newBalance(String ownerType, String ownerName, Long ownerId, String balanceType, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(ownerId);
        jBalanceEntity.setOwnerName(ownerName);
        jBalanceEntity.setOwnerType(ownerType);
        jBalanceEntity.setBalanceType(balanceType);
        jBalanceEntity.setCurrency(currency);
        jBalanceDao.insert(jBalanceEntity);
    }


    /**
     * 记账流水
     */
    private JLogEntity getLogEntity(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity entity = new JLogEntity();
        entity.setOwnerId(balance.getOwnerId());
        entity.setOwnerName(balance.getOwnerName());
        entity.setOwnerType(balance.getOwnerType());
        entity.setBalanceId(balance.getId());
        entity.setBalanceType(balance.getBalanceType());
        entity.setCurrency(balance.getCurrency());
        entity.setFactAmount(factAmount);
        entity.setFactMemo(factMemo);
        entity.setFactId(factId);
        entity.setOriginType(originType);
        entity.setFactType(factType);
        entity.setOldBalance(balance.getBalance());
        entity.setNewBalance(balance.getBalance().add(factAmount));
        entity.setVersion(balance.getVersion() + 1L);
        return entity;
    }

    /**
     * 冻结confirm流水
     */
    private JLogEntity getFreezeConfirmLogEntity(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity entity = new JLogEntity();
        entity.setOwnerId(balance.getOwnerId());
        entity.setOwnerName(balance.getOwnerName());
        entity.setOwnerType(balance.getOwnerType());
        entity.setBalanceId(balance.getId());
        entity.setBalanceType(balance.getBalanceType());
        entity.setCurrency(balance.getCurrency());
        entity.setFactAmount(factAmount);

        entity.setFactMemo(factMemo + "|冻结户:" + balance.getFrozen() + " - " + factAmount + " = " + balance.getFrozen().subtract(factAmount));

        entity.setOriginType(originType);
        entity.setFactId(factId);
        entity.setFactType(factType);

        entity.setOldBalance(balance.getBalance());
        entity.setNewBalance(balance.getBalance());
        entity.setVersion(balance.getVersion() + 1L);
        return entity;
    }

    /**
     * 冻结
     *
     * @return
     */
    public LambdaUpdateWrapper<JBalanceEntity> freeze(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getLogEntity(balance, originType, factType, factId, factMemo, factAmount.negate());
        try {
            jLogDao.insert(logEntity);
        } catch (DuplicateKeyException ex) {
            ex.printStackTrace();
            throw new RenException("重复记账");
        }
        LambdaUpdateWrapper<JBalanceEntity> wrapper = Wrappers.<JBalanceEntity>lambdaUpdate()
                .eq(JBalanceEntity::getId, balance.getId())
                .eq(JBalanceEntity::getVersion, balance.getVersion())
                .set(JBalanceEntity::getVersion, balance.getVersion() + 1)
                .set(JBalanceEntity::getBalance, logEntity.getNewBalance())
                .set(JBalanceEntity::getFrozen, balance.getFrozen().add(factAmount))
                .set(JBalanceEntity::getUpdateDate, new Date());
        return wrapper;
    }

    /**
     * 冻结
     *
     * @return
     */
    public void freezeUpdate(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> freeze = this.freeze(balance, originType, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, freeze);
        if (update != 1) {
            throw new RenException("冻结失败");
        }
    }

    /**
     * 冻结确认
     */
    public LambdaUpdateWrapper<JBalanceEntity> confirm(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {

//        JLogEntity logEntity = getFreezeConfirmLogEntity(balance, originType, factType, factId, factMemo, factAmount);
//        try {
//            jLogDao.insert(logEntity);
//        } catch (DuplicateKeyException ex) {
//            ex.printStackTrace();
//            throw new RenException("重复记账");
//        }
        LambdaUpdateWrapper<JBalanceEntity> wrapper = Wrappers.<JBalanceEntity>lambdaUpdate()
                .eq(JBalanceEntity::getId, balance.getId())
                .eq(JBalanceEntity::getVersion, balance.getVersion())
                .set(JBalanceEntity::getVersion, balance.getVersion() + 1)
//                .set(JBalanceEntity::getBalance, logEntity.getNewBalance())
                .set(JBalanceEntity::getFrozen, balance.getFrozen().subtract(factAmount))
                .set(JBalanceEntity::getUpdateDate, new Date());
        return wrapper;
    }

    /**
     *
     */
    public void confirmUpdate(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> confirm = this.confirm(balance, originType, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, confirm);
        if (update != 1) {
            throw new RenException("ledge failed");
        }
    }

    /**
     * 解冻
     */
    public LambdaUpdateWrapper<JBalanceEntity> unFreeze(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getLogEntity(balance, originType, factType, factId, factMemo, factAmount);
        try {
            jLogDao.insert(logEntity);
        } catch (DuplicateKeyException ex) {
            ex.printStackTrace();
            throw new RenException("重复记账");
        }
        LambdaUpdateWrapper<JBalanceEntity> wrapper = Wrappers.<JBalanceEntity>lambdaUpdate()
                .eq(JBalanceEntity::getId, balance.getId())
                .eq(JBalanceEntity::getVersion, balance.getVersion())
                .set(JBalanceEntity::getVersion, balance.getVersion() + 1)
                .set(JBalanceEntity::getBalance, logEntity.getNewBalance())
                .set(JBalanceEntity::getFrozen, balance.getFrozen().subtract(factAmount))
                .set(JBalanceEntity::getUpdateDate, new Date());
        return wrapper;
    }

    public void unFreezeUpdate(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> unFreeze = this.unFreeze(balance, originType, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, unFreeze);
        if (update != 1) {
            throw new RenException("ledge failed");
        }
    }


    /**
     * 记账更新条件
     */
    public LambdaUpdateWrapper<JBalanceEntity> ledge(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {

        JLogEntity logEntity = getLogEntity(balance, originType, factType, factId, factMemo, factAmount);
        try {
            jLogDao.insert(logEntity);
        } catch (DuplicateKeyException ex) {
            ex.printStackTrace();
            throw new RenException("重复记账");
        }
        LambdaUpdateWrapper<JBalanceEntity> wrapper = Wrappers.<JBalanceEntity>lambdaUpdate()
                .eq(JBalanceEntity::getId, balance.getId())
                .eq(JBalanceEntity::getVersion, balance.getVersion())
                .set(JBalanceEntity::getVersion, balance.getVersion() + 1)
                .set(JBalanceEntity::getBalance, logEntity.getNewBalance())
                .set(JBalanceEntity::getUpdateDate, new Date());
        return wrapper;

    }

    /**
     * 直接记账
     */
    public void ledgeUpdate(JBalanceEntity balance, int originType, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> ledge = this.ledge(balance, originType, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, ledge);
        if (update != 1) {
            throw new RenException("ledge failed");
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // 商户va
    public JBalanceEntity getVaAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getVaAccount(currency))
        );
    }

    ////////////////////////////////////////////////////////////////////////
    // 子商户-va
    public JBalanceEntity getSubVaAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getSubVaAccount(currency))
        );
    }

    // 子商户-保证金
    public JBalanceEntity getDepositAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getDepositAccount(currency))
        );
    }

    // 子商户-卡充值手续费账户
    public JBalanceEntity getChargeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getChargeAccount(currency))
        );
    }

    // 子商户-卡汇总金额账户
    public JBalanceEntity getCardSumAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getCardSumAccount(currency))
        );
    }

    // 子商户-发卡总额
    public JBalanceEntity getCardCountAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getCardCountAccount(currency))
        );
    }

    // 子商户-开卡费账户
    public JBalanceEntity getCardFeeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getCardFeeAccount(currency))
        );
    }

    // 子商户-交易手续费账户
    public JBalanceEntity getTxnAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getTxnAccount(currency))
        );
    }

    ////////////////////////////////////////////////////////////////////////
    // 预付费主卡-额度账户
    public JBalanceEntity getPrepaidQuotaAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getPrepaidQuotaAccount(currency))
        );
    }

    // 预付费主卡-发卡总额
    public JBalanceEntity getPrepaidSumAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getPrepaidSumAccount(currency))
        );
    }

    ////////////////////////////////////////////////////////////////////////
    // 累计通联-发起充值金额
    public JBalanceEntity getAipCardSumAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getAipCardSumAccount(currency))
        );
    }

    // 累计通联-手开卡费用
    public JBalanceEntity getAipCardFeeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getAipCardFeeAccount(currency))
        );
    }

    // 累计通联-保证金金额
    public JBalanceEntity getAipDepositAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getAipDepositAccount(currency))
        );
    }

    // 累计通联-手续费金额
    public JBalanceEntity getAipChargeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getAipChargeAccount(currency))
        );
    }

    // 累计通联-交易手续费
    public JBalanceEntity getAipTxnAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getAipTxnAccount(currency))
        );
    }

//    // 钱包主卡 额度账户
//    public JBalanceEntity getWalletQuotaAccount(Long ownerId, String currency) {
//        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
//                .eq(JBalanceEntity::getOwnerId, ownerId)
//                .eq(JBalanceEntity::getBalanceType, BalanceType.getWalletQuotaAccount(currency))
//        );
//    }

    // 钱包发卡总额
    public JBalanceEntity getWalletSumAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getWalletSumAccount(currency))
        );
    }

    // 钱包账户
    public JBalanceEntity getWalletAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getWalletAccount(currency))
        );
    }

}
