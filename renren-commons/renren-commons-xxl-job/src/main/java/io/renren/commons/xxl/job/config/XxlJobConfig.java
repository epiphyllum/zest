/**
 * Copyright (c) 2016-2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.commons.xxl.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import io.renren.commons.xxl.job.properties.XxlJobProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@Configuration
public class XxlJobConfig {
    @Resource
    private XxlJobProperties properties;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(properties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(properties.getExecutor().getAppName());
        xxlJobSpringExecutor.setIp(properties.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(properties.getExecutor().getPort());
        xxlJobSpringExecutor.setAccessToken(properties.getExecutor().getAccessToken());
        xxlJobSpringExecutor.setLogPath(properties.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(properties.getExecutor().getLogRetentionDays());

        return xxlJobSpringExecutor;
    }
}
