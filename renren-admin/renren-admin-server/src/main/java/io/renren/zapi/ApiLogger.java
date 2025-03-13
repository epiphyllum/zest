package io.renren.zapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.ExceptionUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JPacketDao;
import io.renren.zadmin.entity.JPacketEntity;
import io.renren.zcommon.ZestConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ApiLogger {
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private JPacketDao jPacketDao;

    /**
     * 记录正常日志
     * @param packetEntity
     * @param result
     */
    public void logPacketSuccess(JPacketEntity packetEntity, Result<?> result) {
        CompletableFuture.runAsync(() -> {
            try {
                String send = objectMapper.writeValueAsString(result);
                send = send.length() >= 2047 ? send.substring(0, 2047) : send;
                packetEntity.setSend(send);
                jPacketDao.insert(packetEntity);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        });
    }

    /**
     * 记录正常日志
     * @param packetEntity
     */
    public void logPacketSuccess(JPacketEntity packetEntity) {
        CompletableFuture.runAsync(() -> {
            jPacketDao.insert(packetEntity);
        });
    }

    /**
     * 记录日志异常
     * @param packetEntity
     * @param failEx
     */
    public void logPacketException(JPacketEntity packetEntity, Exception failEx) {
        // 记录日志
        CompletableFuture.runAsync(() -> {
            try {
                String errorStackTrace = ExceptionUtils.getErrorStackTrace(failEx);
                String send = errorStackTrace.length() >= 2047 ? errorStackTrace.substring(0, 2048) : errorStackTrace;
                packetEntity.setSend(send);
                jPacketDao.insert(packetEntity);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

}
