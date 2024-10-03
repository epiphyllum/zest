package io.renren.zbalance;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JLogDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JLogEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
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
     * 记账更新条件
     */
    public LambdaUpdateWrapper<JBalanceEntity> ledge(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getLogEntity(balance, factType, factId, factMemo, factAmount);
        jLogDao.insert(logEntity);
        LambdaUpdateWrapper<JBalanceEntity> wrapper = Wrappers.<JBalanceEntity>lambdaUpdate()
                .eq(JBalanceEntity::getId, balance.getId())
                .eq(JBalanceEntity::getVersion, balance.getVersion())
                .set(JBalanceEntity::getVersion, balance.getVersion() + 1)
                .set(JBalanceEntity::getBalance, logEntity.getNewBalance());
        return wrapper;
    }

    /**
     * 直接记账
     */
    public void update(JBalanceEntity balance, int factType, Long factId, String factMemo, BigDecimal factAmount) {
        JLogEntity logEntity = getLogEntity(balance, factType, factId, factMemo, factAmount);
        jLogDao.insert(logEntity);
        LambdaUpdateWrapper<JBalanceEntity> wrapper = Wrappers.<JBalanceEntity>lambdaUpdate()
                .eq(JBalanceEntity::getId, balance.getId())
                .eq(JBalanceEntity::getVersion, balance.getVersion())
                .set(JBalanceEntity::getVersion, balance.getVersion() + 1)
                .set(JBalanceEntity::getBalance, logEntity.getNewBalance());
        int update = jBalanceDao.update(null, wrapper);
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
