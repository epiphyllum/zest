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
    private ZestConfig zestConfig;
    @Resource
    private JConfigDao jConfigDao;
    @Resource
    private JVaManager jVaManager;

    @Override
    public void run(String... args) throws Exception {
        initConfig();
//        jVaManager.refresh(); // 更新余额
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
