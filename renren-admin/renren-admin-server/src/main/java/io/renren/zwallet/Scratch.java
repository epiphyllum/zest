package io.renren.zwallet;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping("zwallet/scratch")
public class Scratch {
    @GetMapping("mail")
    public String qqMail() {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.qq.com");
        sender.setProtocol("smtp");
        sender.setPort(465);
        sender.setUsername("94093146@qq.com");
        sender.setPassword("smntmpplzfbzbhed");  // 密码
        sender.setDefaultEncoding("UTF-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.ssl.enable", "true");
        p.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        sender.setJavaMailProperties(p);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("测试标题");
            message.setText("测试正文");
            message.setFrom("94093146@qq.com");
            message.setTo("epiphyllum.zhou@gmail.com");
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }

        return "ok";
    }
}
