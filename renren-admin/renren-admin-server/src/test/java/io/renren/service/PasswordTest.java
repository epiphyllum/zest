package io.renren.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTest {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    public void encode() {
        String password = "123456";
        password = passwordEncoder.encode(password);

        System.out.println(password);
    }

}
