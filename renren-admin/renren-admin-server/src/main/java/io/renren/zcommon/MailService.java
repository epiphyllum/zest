package io.renren.zcommon;

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

    private Map<Long, Pair<JWalletConfigEntity, JavaMailSenderImpl>> mailSenderMap = new ConcurrentHashMap<>();

    private Pair<JWalletConfigEntity, JavaMailSenderImpl> getMailSender(JWalletConfigEntity entity) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(null);
        mailSender.setPort(587);
        mailSender.setUsername(null);
        mailSender.setPassword(null);
        mailSender.setProtocol("smtp");
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.ssl.trust", "smtp.163.com");
        props.put("mail.smtp.ssl.enable", "true");
        mailSender.setJavaMailProperties(props);
        return Pair.of(entity, mailSender);
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
        String from = null;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // 发件人:
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

        String subject = null;
        String content = null;
        String from = null;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        sender.send(message);
    }
}
