package io.renren.zcommon;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import io.renren.commons.tools.exception.RenException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

public class CommonUtils {
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String BASE36_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String base62(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.insert(0, BASE62_CHARS.charAt((int) (number % 62)));
            number = number / 62;
        }
        return sb.toString();
    }

    public static String base36(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.insert(0, BASE36_CHARS.charAt((int) (number % 36)));
            number = number / 36;
        }
        return sb.toString();
    }

    /**
     * 获取访问IP
     *
     * @return
     */
    public static String getIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String value = request.getHeader("x-forwarded-for");
        if (value == null) {
            return request.getRemoteHost();
        }
        int idx = value.indexOf(',');
        if (idx == -1) {
            return value;
        } else {
            return value.substring(0, idx);
        }
    }

    public static String getDomain() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String hostHeader = request.getHeader("Host");
        // 如果 Host 不为空，按照 Host 头的格式提取域名部分
        if (hostHeader != null) {
            String domain = hostHeader.split(":")[0]; // 可能包含端口号，需要去掉端口部分
            return domain;
        }
        throw new RenException("can not get domain");

    }

    public static String yuan(Long fen) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(fen / 100.0);
    }


    private static ConcurrentHashMap<String, Logger> loggerCache = new ConcurrentHashMap<>();

    public static Logger getLogger(String name) {
        Logger cache = loggerCache.get(name);
        if (cache != null) {
            return cache;
        }

        loggerCache.computeIfAbsent(name, x -> {
            System.out.println("new log:" + x);
            Logger logger = (Logger) LoggerFactory.getLogger(name);
            logger.setLevel(Level.INFO);
            // 创建文件追加器
            FileAppender fileAppender = new FileAppender();
            fileAppender.setName(name + "FileAppender");
            fileAppender.setFile("./" + name + ".log");
            fileAppender.setAppend(true);
            fileAppender.setContext(logger.getLoggerContext());

            // 设置格式化
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(fileAppender.getContext());
            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n"); // 日志格式
            encoder.start();
            fileAppender.setEncoder(encoder);
            fileAppender.start();
            logger.addAppender(fileAppender);
            return logger;
        });
        return loggerCache.get(name);
    }

    // 唯一ID生成器
    public static  String newRequestId() {
        // return UUID.randomUUID().toString().replaceAll("-", "");
        return DefaultIdentifierGenerator.getInstance().nextId(null).toString();
    }

}
