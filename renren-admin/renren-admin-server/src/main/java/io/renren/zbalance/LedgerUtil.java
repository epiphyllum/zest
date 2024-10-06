package io.renren.zbalance;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JLogDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JLogEntity;
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
     * 记账流水
     */
    private JLogEntity getLogEntity(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
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
        entity.setFactType(factType);
        entity.setOldBalance(balance.getBalance());
        entity.setNewBalance(balance.getBalance().add(factAmount));
        entity.setVersion(balance.getVersion() + 1L);
        return entity;
    }

    /**
     * 冻结confirm流水
     */
    private JLogEntity getFreezeConfirmLogEntity(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity entity = new JLogEntity();
        entity.setOwnerId(balance.getOwnerId());
        entity.setOwnerName(balance.getOwnerName());
        entity.setOwnerType(balance.getOwnerType());
        entity.setBalanceId(balance.getId());
        entity.setBalanceType(balance.getBalanceType());
        entity.setCurrency(balance.getCurrency());
        entity.setFactAmount(factAmount);

        entity.setFactMemo(factMemo + "|冻结户:" + balance.getFrozen() + " - " + factAmount + " = " + balance.getFrozen().subtract(factAmount));

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
    public LambdaUpdateWrapper<JBalanceEntity> freeze(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getLogEntity(balance, factType, factId, factMemo, factAmount.negate());
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
    public void freezeUpdate(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> freeze = this.freeze(balance, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, freeze);
        if (update != 1) {
            throw new RenException("冻结失败");
        }
    }

    /**
     * 冻结确认
     */
    public LambdaUpdateWrapper<JBalanceEntity> confirm(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getFreezeConfirmLogEntity(balance, factType, factId, factMemo, factAmount);
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

    /**
     *
     */
    public void confirmUpdate(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> confirm = this.confirm(balance, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, confirm);
        if (update != 1) {
            throw new RenException("ledge failed");
        }
    }

    /**
     * 解冻
     */
    public LambdaUpdateWrapper<JBalanceEntity> unFreeze(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getLogEntity(balance, factType, factId, factMemo, factAmount);
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

    public void unFreezeUpdate(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> unFreeze = this.unFreeze(balance, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, unFreeze);
        if (update != 1) {
            throw new RenException("ledge failed");
        }
    }


    /**
     * 记账更新条件
     */
    public LambdaUpdateWrapper<JBalanceEntity> ledge(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {

        JLogEntity logEntity = getLogEntity(balance, factType, factId, factMemo, factAmount);
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
    public void ledgeUpdate(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        LambdaUpdateWrapper<JBalanceEntity> ledge = this.ledge(balance, factType, factId, factMemo, factAmount);
        int update = jBalanceDao.update(null, ledge);
        if (update != 1) {
            throw new RenException("ledge failed");
        }
    }

    // 入金账户
    public JBalanceEntity getInAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getInAccount(currency))
        );
    }

    // 商户va
    public JBalanceEntity getVaAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getVaAccount(currency))
        );
    }

    // 商户预收保证金
    public JBalanceEntity getDepositAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getDepositAccount(currency))
        );
    }

    // 商户卡充值手续费账户
    public JBalanceEntity getChargeFeeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getChargeFeeAccount(currency))
        );
    }

    // 商户交易手续费账户
    public JBalanceEntity getTxnFeeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getTxnFeeAccount(currency))
        );
    }

    // 子商户va
    public JBalanceEntity getSubVaAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getSubVaAccount(currency))
        );
    }

    // 子商户卡汇总金额账户
    public JBalanceEntity getSubSumAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getSubSumAccount(currency))
        );
    }

    // 子商户开卡费账户
    public JBalanceEntity getSubFeeAccount(Long ownerId, String currency) {
        return jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, ownerId)
                .eq(JBalanceEntity::getBalanceType, BalanceType.getSubFeeAccount(currency))
        );
    }
}
