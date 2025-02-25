package io.renren;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JConfigDao;
import io.renren.zadmin.entity.JConfigEntity;
import io.renren.zcommon.ZestConfig;
import io.renren.zmanager.JVaManager;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class InitRun implements CommandLineRunner {
    @Resource
    private JConfigDao jConfigDao;

    @Resource
    private ZestConfig zestConfig;

    @Override
    public void run(String... args) throws Exception {
        initConfig();
    }

    // 平台配置
    public void initConfig() {
        System.out.println("创建平台配置....");
        List<JConfigEntity> jConfigEntities = jConfigDao.selectList(Wrappers.emptyWrapper());
        if (jConfigEntities.size() > 0) {
            // 已经有配置
            return;
        }

        //
        JConfigEntity entity = new JConfigEntity();
        String vccMainReal = zestConfig.getVccMainReal() == null ? "0000" : zestConfig.getVccMainReal();
        String vccMainVirtual = zestConfig.getVccMainVirtual() == null ? "0000" : zestConfig.getVccMainVirtual();

        entity.setQuotaLimit(100);
        entity.setVccMainReal(vccMainReal);        // 实体卡主卡
        entity.setVccMainVirtual(vccMainVirtual);  // 虚拟卡主卡
        jConfigDao.insert(entity);
    }

}
