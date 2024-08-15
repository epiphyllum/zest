/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.route;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Nacos动态路由支持
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {
    private final Logger log = LoggerFactory.getLogger(NacosRouteDefinitionRepository.class);
    private final ApplicationEventPublisher publisher;
    private final NacosConfigProperties nacosConfigProperties;
    private final NacosConfigManager nacosConfigManager;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Nacos DATA_ID
     */
    private static final String DATA_ID = "gateway.json";

    public NacosRouteDefinitionRepository(ApplicationEventPublisher publisher, NacosConfigProperties nacosConfigProperties) {
        this.publisher = publisher;
        this.nacosConfigProperties = nacosConfigProperties;
        this.nacosConfigManager = new NacosConfigManager(nacosConfigProperties);
        nacosListener();
    }

    /**
     * Nacos监听器
     */
    private void nacosListener() {
        try {
            nacosConfigManager.getConfigService().addListener(DATA_ID, nacosConfigProperties.getGroup(), new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                }
            });
        } catch (NacosException e) {
            log.error("nacosListener error", e);
        }
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            String routeConfig = nacosConfigManager.getConfigService()
                    .getConfig(DATA_ID, nacosConfigProperties.getGroup(), 3000);

            List<RouteDefinition> routeDefinitionList = new ArrayList<>();
            if (StringUtils.hasText(routeConfig)) {
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                routeDefinitionList = objectMapper.readValue(routeConfig, new TypeReference<>() {
                });
            }

            return Flux.fromIterable(routeDefinitionList);
        } catch (Exception e) {
            log.error("getRouteDefinitions error ", e);
        }

        return Flux.fromIterable(new ArrayList<>());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}