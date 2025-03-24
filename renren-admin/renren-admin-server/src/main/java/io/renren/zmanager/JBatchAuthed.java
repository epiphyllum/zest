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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class JBatchAuthed extends JBatchBase {
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JBatchDao jBatchDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JAuthedDao jAuthedDao;
    @Resource
    private JAuthedService jAuthedService;
    @Resource
    private ZinCardTxnService zinCardTxnService;

    // 结算流水下载
    public void authedBatch(String dateStr) {

        Date date = DateUtils.parse(dateStr, DateUtils.DATE_PATTERN);
        // 创建任务记录
        log.info("创建任务: {}, {}", date, ZestConstant.BATCH_TYPE_AUTHED);
        JBatchEntity batchEntity = newBatchItem(date, ZestConstant.BATCH_TYPE_AUTHED);

        // 第一次下载
        String entryDate = dateStr.replaceAll("-", "");
        TAuthSettledQuery request = new TAuthSettledQuery();
        request.setPagesize(100);
        request.setPageindex(1);
        request.setEntrydate(entryDate);
        TAuthSettledResponse response = zinCardTxnService.settledQuery(request);
        int downloaded = response.getDatalist().size();
        int total = response.getTotal();
        int batchSize = 1000;
        int batchCount = total / 1000 + 1;
        if (total == 0) {
            log.info("没有流水");
            return;
        }
        // 批次
        List<List<JAuthedEntity>> batchList = new ArrayList<>(batchCount);

        // 准备批次
        List<JAuthedEntity> curBatch = new ArrayList<>(1000);
        for (TAuthSettledResponse.Item item : response.getDatalist()) {
            JAuthedEntity entity = ConvertUtils.sourceToTarget(item, JAuthedEntity.class);

            // 补充卡信息
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            entity.setWalletId(cardEntity.getWalletId());
            entity.setWalletName(cardEntity.getWalletName());

            entity.setSubId(cardEntity.getSubId());
            entity.setSubName(cardEntity.getSubName());
            entity.setMerchantId(cardEntity.getMerchantId());
            entity.setMerchantName(cardEntity.getMerchantName());
            entity.setAgentId(cardEntity.getAgentId());
            entity.setAgentName(cardEntity.getAgentName());

            entity.setMarketproduct(cardEntity.getMarketproduct());
            entity.setMaincardno(cardEntity.getMaincardno());

            curBatch.add(entity);
            if (curBatch.size() == batchSize) {
                batchList.add(curBatch);
                curBatch = new ArrayList<>(1000);
            }
        }

        while (downloaded < total) {
            request.setPageindex(request.getPageindex() + 1);
            response = zinCardTxnService.settledQuery(request);
            downloaded += response.getDatalist().size();
            for (TAuthSettledResponse.Item item : response.getDatalist()) {
                JAuthedEntity entity = ConvertUtils.sourceToTarget(item, JAuthedEntity.class);
                curBatch.add(entity);
                if (curBatch.size() == batchSize) {
                    batchList.add(curBatch);
                    curBatch = new ArrayList<>(1000);
                }
            }
        }
        if (curBatch.size() < batchSize) {
            batchList.add(curBatch);
        }

        boolean success = true;

        // 开始插入批次
        try {
            for (List<JAuthedEntity> batch : batchList) {
                tx.executeWithoutResult(st -> {
                    try {
                        jAuthedService.insertBatch(batch);
                    } catch (Exception ex) {
                        for (JAuthedEntity jAuthedEntity : batch) {
                            try {
                                log.info("insert -> {}", jAuthedEntity);
                                jAuthedDao.insert(jAuthedEntity);
                            } catch (DuplicateKeyException e) {
                            }
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            success = false;
        }

        String state = success ? ZestConstant.BATCH_STATUS_SUCCESS : ZestConstant.BATCH_STATUS_FAIL;
        jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                .eq(JBatchEntity::getId, batchEntity.getId())
                .eq(JBatchEntity::getState, batchEntity.getState())
                .set(JBatchEntity::getState, state)
                .set(JBatchEntity::getMemo, "记录:" + total));

    }
}
