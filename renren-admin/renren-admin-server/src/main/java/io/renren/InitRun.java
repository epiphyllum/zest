package io.renren;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.dao.SysDeptDao;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JConfigDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JConfigEntity;
import io.renren.zadmin.service.JBalanceService;
import io.renren.zbalance.BalanceType;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InitRun implements CommandLineRunner {
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JConfigDao jConfigDao;
    @Resource
    private TransactionTemplate tx;

    @Override
    public void run(String... args) throws Exception {
        initPlatformBalance();
        initConfig();
    }

    public void initConfig() {
        System.out.println("创建平台配置....");
        List<JConfigEntity> jConfigEntities = jConfigDao.selectList(Wrappers.emptyWrapper());
        if (jConfigEntities.size() > 0) {
            System.out.println("已经创建， 无需创建");
            return;
        }
        JConfigEntity entity = new JConfigEntity();
        entity.setQuotaLimit(100);
        entity.setVccMainReal("0000");
        entity.setVccMainVirtual("0000");
        jConfigDao.insert(entity);
    }

    /**
     * 初始化平台账户
     */
    public void initPlatformBalance() {

        System.out.println("创建平台账户....");
        List<JBalanceEntity> jBalanceEntities = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, 0L)
        );
        if (jBalanceEntities.size() > 0) {
            System.out.println("已经创建， 无需创建");
            return;
        }

        tx.executeWithoutResult(status -> {
            for (String currency : BalanceType.CURRENCY_LIST) {
                // 手续费总额
                JBalanceEntity aipCharge = new JBalanceEntity();
                aipCharge.setBalance(BigDecimal.ZERO);
                aipCharge.setBalanceType(BalanceType.getAipChargeAccount(currency));
                aipCharge.setCurrency(currency);
                aipCharge.setOwnerId(0L);
                aipCharge.setOwnerType("platform");
                aipCharge.setOwnerName("平台");
                // 保证金总额
                JBalanceEntity aipDeposit = new JBalanceEntity();
                aipDeposit.setBalance(BigDecimal.ZERO);
                aipDeposit.setBalanceType(BalanceType.getAipDepositAccount(currency));
                aipDeposit.setCurrency(currency);
                aipDeposit.setOwnerId(0L);
                aipDeposit.setOwnerType("platform");
                aipDeposit.setOwnerName("平台");

                // 充值发起总额
                JBalanceEntity aipCardSum = new JBalanceEntity();
                aipCardSum.setBalance(BigDecimal.ZERO);
                aipCardSum.setBalanceType(BalanceType.getAipCardSumAccount(currency));
                aipCardSum.setCurrency(currency);
                aipCardSum.setOwnerId(0L);
                aipCardSum.setOwnerType("platform");
                aipCardSum.setOwnerName("平台");

                // 开卡费总额
                JBalanceEntity aipCardFee = new JBalanceEntity();
                aipCardFee.setBalance(BigDecimal.ZERO);
                aipCardFee.setBalanceType(BalanceType.getAipCardFeeAccount(currency));
                aipCardFee.setCurrency(currency);
                aipCardFee.setOwnerId(0L);
                aipCardFee.setOwnerType("platform");
                aipCardFee.setOwnerName("平台");

                // 交易费用
                JBalanceEntity aipTxn = new JBalanceEntity();
                aipTxn.setBalance(BigDecimal.ZERO);
                aipTxn.setBalanceType(BalanceType.getAipTxnAccount(currency));
                aipTxn.setCurrency(currency);
                aipTxn.setOwnerId(0L);
                aipTxn.setOwnerType("platform");
                aipTxn.setOwnerName("平台");

                jBalanceDao.insert(aipCharge);
                jBalanceDao.insert(aipCardSum);
                jBalanceDao.insert(aipCardFee);
                jBalanceDao.insert(aipDeposit);
                jBalanceDao.insert(aipTxn);
            }
        });
    }
}
