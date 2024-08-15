/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.commons.log.aspect;

import io.renren.commons.log.SysLogOperation;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.log.enums.LogTypeEnum;
import io.renren.commons.log.enums.OperationStatusEnum;
import io.renren.commons.log.producer.LogProducer;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.config.ModuleConfig;
import io.renren.commons.tools.utils.HttpContextUtils;
import io.renren.commons.tools.utils.IpUtils;
import io.renren.commons.tools.utils.JsonUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志，切面处理类
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Aspect
@Component
public class LogOperationAspect {
    @Resource
    private ModuleConfig moduleConfig;
    @Resource
    private LogProducer logProducer;

    @Pointcut("@annotation(io.renren.commons.log.annotation.LogOperation)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        try {
            //执行方法
            Object result = point.proceed();

            //执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;
            //保存日志
            saveLog(point, time, OperationStatusEnum.SUCCESS.value());

            return result;
        } catch (Exception e) {
            //执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;
            //保存日志
            saveLog(point, time, OperationStatusEnum.FAIL.value());

            throw e;
        }
    }


    private void saveLog(ProceedingJoinPoint joinPoint, long time, Integer status) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLogOperation log = new SysLogOperation();
        LogOperation annotation = method.getAnnotation(LogOperation.class);
        if (annotation != null) {
            //注解上的描述
            log.setOperation(annotation.value());
        }

        //登录用户信息
        UserDetail user = SecurityUser.getUser();
        if (user != null) {
            log.setCreator(user.getId());
            log.setCreatorName(user.getUsername());
        }

        log.setType(LogTypeEnum.OPERATION.value());
        log.setModule(moduleConfig.getName());
        log.setStatus(status);
        log.setRequestTime((int) time);
        log.setCreateDate(new Date());

        //请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.setIp(IpUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setRequestUri(request.getRequestURI());
        log.setRequestMethod(request.getMethod());

        //请求参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JsonUtils.toJsonString(args[0]);
            log.setRequestParams(params);
        } catch (Exception e) {

        }

        //保存到Redis队列里
        logProducer.saveLog(log);
    }
}