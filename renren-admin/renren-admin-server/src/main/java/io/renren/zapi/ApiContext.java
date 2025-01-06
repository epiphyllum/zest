package io.renren.zapi;

import ch.qos.logback.classic.Logger;
import com.alibaba.ttl.TransmittableThreadLocal;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.account.dto.MoneyAccountAdd;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiContext {
    private static final ThreadLocal<ApiContext> API_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    // 上下文
    public static void setContext(ApiContext context) {
        API_CONTEXT_THREAD_LOCAL.set(context);
    }
    public static ApiContext getContext() {
        return API_CONTEXT_THREAD_LOCAL.get();
    }
    public static void clear() {
        API_CONTEXT_THREAD_LOCAL.remove();
    }

    //
    JMerchantEntity merchant;  // 哪个商户
    Logger logger;  // 日志对象

    public void error(String format, Object arg) {
        logger.error(format, arg);
    }
    public void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }
    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }
    public void info(String format, Object arg) {
        logger.info(format, arg);
    }
    public void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }
    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }
    public void warn(String format, Object arg) {
        logger.warn(format, arg);
    }
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(format, arg1, arg2);
    }
    public void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }
    public void debug(String format, Object arg) { logger.debug(format, arg); }
    public void debug(String format, Object arg1, Object arg2) { logger.debug(format, arg1, arg2); }
    public void debug(String format, Object... arguments) { logger.debug(format, arguments); }
}
