package io.renren;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.Result;
import io.renren.dao.SysDeptDao;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.entity.JBalanceEntity;
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
    private JBalanceService jBalanceService;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private TransactionTemplate tx;

    @Override
    public void run(String... args) throws Exception {
        initPlatformBalance();
    }

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
                JBalanceEntity feeSum = new JBalanceEntity();
                feeSum.setBalance(BigDecimal.ZERO);
                feeSum.setBalanceType(BalanceType.getFeeSumAccount(currency));
                feeSum.setCurrency(currency);
                feeSum.setOwnerId(0L);
                feeSum.setOwnerType("platform");
                feeSum.setOwnerName("平台");

                JBalanceEntity depositSum = new JBalanceEntity();
                depositSum.setBalance(BigDecimal.ZERO);
                depositSum.setBalanceType(BalanceType.getDepositSumAccount(currency));
                depositSum.setCurrency(currency);
                depositSum.setOwnerId(0L);
                depositSum.setOwnerType("platform");
                depositSum.setOwnerName("平台");

                JBalanceEntity chargeSum = new JBalanceEntity();
                chargeSum.setBalance(BigDecimal.ZERO);
                chargeSum.setBalanceType(BalanceType.getChargeSumAccount(currency));
                chargeSum.setCurrency(currency);
                chargeSum.setOwnerId(0L);
                chargeSum.setOwnerType("platform");
                chargeSum.setOwnerName("平台");

                jBalanceDao.insert(feeSum);
                jBalanceDao.insert(depositSum);
                jBalanceDao.insert(chargeSum);
            }
        });
    }
}
