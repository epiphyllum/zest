package io.renren.zmanager;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.DateUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zadmin.service.JAuthedService;
import io.renren.zadmin.service.JStatService;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZestConstant;
import io.renren.zin.cardtxn.ZinCardTxnService;
import io.renren.zin.cardtxn.dto.TAuthSettledQuery;
import io.renren.zin.cardtxn.dto.TAuthSettledResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;

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
