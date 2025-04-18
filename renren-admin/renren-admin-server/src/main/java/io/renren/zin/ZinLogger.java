package io.renren.zin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.ExceptionUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JChannelLogDao;
import io.renren.zadmin.entity.JChannelLogEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ZinLogger {
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JChannelLogDao jChannelLogDao;

    /**
     * 记录异常日志
     * @param logEntity
     * @param result
     */
    public void logPacketSuccess(JChannelLogEntity logEntity, Result<?> result) {
        CompletableFuture.runAsync(() -> {
            try {
                String jsonStr = objectMapper.writeValueAsString(result);
                logEntity.setSend(jsonStr.length() >= 2047 ? jsonStr.substring(0,2047) : jsonStr);
                jChannelLogDao.insert(logEntity);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        });
    }

    /**
     * 记录正常日志
     * @param logEntity
     */
    public void logPacketSuccess(JChannelLogEntity logEntity) {
        CompletableFuture.runAsync(() -> {
            jChannelLogDao.insert(logEntity);
        });
    }

    /**
     * 记录日志异常
     * @param logEntity
     * @param failEx
     */
    public void logPacketException(JChannelLogEntity logEntity, Exception failEx) {
        // 记录日志
        CompletableFuture.runAsync(() -> {
            try {
                String errorStackTrace = ExceptionUtils.getErrorStackTrace(failEx);
                String send = errorStackTrace.length() >= 2047 ? errorStackTrace.substring(0,2047) : errorStackTrace;
                logEntity.setSend(send);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            jChannelLogDao.insert(logEntity);
        });
    }

}
