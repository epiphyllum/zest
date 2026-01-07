package io.renren.zmanager;

import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZestConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class JBatchManager {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JBatchDao jBatchDao;
    @Resource
    private JBatchAuthed jBatchAuthed;
    @Resource
    private JBatchAuthedFile jBatchAuthedFile;
    @Resource
    private JBatchStat jBatchStat;
    @Resource
    private JBatchAuth jBatchAuth;
    @Resource
    private JBatchAuthFile jBatchAuthFile;


    /**
     * 批处理任务
     */
    public void run(String batchType, String date) {
        if (batchType.equals(ZestConstant.BATCH_TYPE_STAT)) {
            jBatchStat.statBatch(date);
            return;
        }
        if (batchType.equals(ZestConstant.BATCH_TYPE_AUTHED)) {
            jBatchAuthed.authedBatch(date);
            return;
        }
        if (batchType.equals(ZestConstant.BATCH_TYPE_AUTHED_FILE)) {
            jBatchAuthedFile.authedFileBatch(date);
        }

        // 同步授权流水
        if (batchType.equals(ZestConstant.BATCH_TYPE_AUTH)) {
            jBatchAuth.authBatch(date);
        }

        // 授权流水文件生产
        if (batchType.equals(ZestConstant.BATCH_TYPE_AUTH_FILE)) {
            jBatchAuthFile.authFileBatch(date);
        }

    }

    /**
     * 重新运行批处理
     */
    public void rerun(Long id) {
        try {
            JBatchEntity batchEntity = jBatchDao.selectById(id);
            String dateStr = batchEntity.getBatchDate();
            run(batchEntity.getBatchType(), dateStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
