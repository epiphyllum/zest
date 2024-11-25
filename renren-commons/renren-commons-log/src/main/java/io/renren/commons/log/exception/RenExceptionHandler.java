/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.commons.log.exception;

import cn.hutool.core.map.MapUtil;
import io.renren.commons.log.SysLogError;
import io.renren.commons.log.enums.LogTypeEnum;
import io.renren.commons.log.producer.LogProducer;
import io.renren.commons.tools.config.ModuleConfig;
import io.renren.commons.tools.exception.ErrorCode;
import io.renren.commons.tools.exception.ExceptionUtils;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.HttpContextUtils;
import io.renren.commons.tools.utils.IpUtils;
import io.renren.commons.tools.utils.JsonUtils;
import io.renren.commons.tools.utils.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.Map;


/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestControllerAdvice
public class RenExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RenExceptionHandler.class);
    @Resource
    private ModuleConfig moduleConfig;
    @Resource
    private LogProducer logProducer;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RenException.class)
    public Result handleRRException(RenException ex) {
        Result result = new Result();
        result.error(ex.getCode(), ex.getMsg());
        return result;
    }

    // 缺少请求头
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Result handleDuplicateKeyException(MissingRequestHeaderException ex) {
        Result result = new Result();
        result.error("缺少请求头:" + ex.getHeaderName());
        return result;
    }

    // 请求方法不支持
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class )
    public Result handleHttpMethodException(HttpRequestMethodNotSupportedException ex) {
        Result result = new Result();
        result.error("不支持请求方法:" + ex.getMethod());
        return result;
    }

    // 唯一索引
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException ex) {
        Result result = new Result();
        result.error(ErrorCode.DB_RECORD_EXISTS);
        return result;
    }

    // 没有权限
    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(Exception ex) {
        Result result = new Result();
        result.error(ErrorCode.FORBIDDEN);
        return result;
    }

    /**
     * 未预期的异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        saveLog(ex);
        return new Result().error();
    }

    /**
     * 保存异常日志
     */
    private void saveLog(Exception ex) {
        SysLogError log = new SysLogError();
        log.setType(LogTypeEnum.ERROR.value());
        log.setModule(moduleConfig.getName());

        //请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setRequestUri(request.getRequestURI());
        log.setRequestMethod(request.getMethod());
        log.setIp(IpUtils.getIpAddr(request));
        Map<String, String> params = HttpContextUtils.getParameterMap(request);
        if (MapUtil.isNotEmpty(params)) {
            log.setRequestParams(JsonUtils.toJsonString(params));
        }
        //异常信息
        log.setErrorInfo(ExceptionUtils.getErrorStackTrace(ex));
        //保存到Redis队列里
        log.setCreateDate(new Date());
        logProducer.saveLog(log);
    }
}