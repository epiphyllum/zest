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

}
