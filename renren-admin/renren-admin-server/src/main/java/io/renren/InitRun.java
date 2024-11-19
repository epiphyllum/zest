package io.renren;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zmanager.JBatchManager;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InitRun implements CommandLineRunner {
    @Resource
    private JConfigDao jConfigDao;
    @Resource
    private JBatchManager jStatManager;

    @Override
    public void run(String... args) throws Exception {
        initConfig();
        jStatManager.statBatch("2024-11-18");
        jStatManager.statBatch("2024-11-16");
        jStatManager.statBatch("2024-11-15");
    }

    public void initConfig() {
        System.out.println("创建平台配置....");
        List<JConfigEntity> jConfigEntities = jConfigDao.selectList(Wrappers.emptyWrapper());
        if (jConfigEntities.size() > 0) {
            return;
        }
        JConfigEntity entity = new JConfigEntity();
        entity.setQuotaLimit(100);
        entity.setVccMainReal("0000");
        entity.setVccMainVirtual("0000");
        jConfigDao.insert(entity);
    }
}
