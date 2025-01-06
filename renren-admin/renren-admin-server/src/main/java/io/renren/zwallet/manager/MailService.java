package io.renren.zwallet.manager;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.entity.JWalletConfigEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MailService {

    @Resource
    private JWalletConfigDao jWalletConfigDao;

    // 子商户的邮箱发送配置
    private Map<Long, Pair<JWalletConfigEntity, JavaMailSenderImpl>> mailSenderMap = new ConcurrentHashMap<>();

    // 配置邮箱
    private Pair<JWalletConfigEntity, JavaMailSenderImpl> getMailSender(JWalletConfigEntity entity) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(entity.getMailHost());
        sender.setPort(Integer.parseInt(entity.getMailPort()));
        sender.setUsername(entity.getMailUser());
        sender.setPassword(entity.getMailPass());
        sender.setProtocol("smtp");
        sender.setDefaultEncoding("UTF-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.ssl.enable", "true");
        p.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sender.setJavaMailProperties(p);
        return Pair.of(entity, sender);
    }

    // 修改渠道配置
    public void addOperation(JWalletConfigEntity entity) {
        mailSenderMap.put(entity.getSubId(), getMailSender(entity));
    }

    @PostConstruct
    public void init() {
        List<JWalletConfigEntity> configEntities = jWalletConfigDao.selectList(Wrappers.<JWalletConfigEntity>emptyWrapper());
        for (JWalletConfigEntity configEntity : configEntities) {
            mailSenderMap.put(configEntity.getSubId(), getMailSender(configEntity));
        }
    }

    // 发送邮件
    public void sendMail(Long subId, String to, String subject, String content) {
        Pair<JWalletConfigEntity, JavaMailSenderImpl> item = mailSenderMap.get(subId);
        JWalletConfigEntity entity = item.getKey();
        JavaMailSenderImpl sender = item.getValue();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(entity.getMailFrom()); // 发件人:
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        sender.send(message);
    }

    // 发送OTP
    public void sendOTP(Long subId, String to, String otp) {
        Pair<JWalletConfigEntity, JavaMailSenderImpl> item = mailSenderMap.get(subId);
        JWalletConfigEntity entity = item.getKey();
        JavaMailSenderImpl sender = item.getValue();

        String subject = "OTP";
        String content = "OTP is " + otp;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(entity.getMailFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        sender.send(message);
    }
}
