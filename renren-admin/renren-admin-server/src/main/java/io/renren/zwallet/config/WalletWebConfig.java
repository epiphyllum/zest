package io.renren.zwallet.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WalletWebConfig implements WebMvcConfigurer {
    @Resource
    private WalletLoginInterceptor walletLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(walletLoginInterceptor)
                .addPathPatterns("/zwallet/api/**")
                .excludePathPatterns(
                        "/zwallet/api/user/login",
                        "/zwallet/api/user/register",
                        "/zwallet/api/user/reset",
                        "/zwallet/api/user/change",
                        "/zwallet/api/user/emailOTP"
                );
    }
}