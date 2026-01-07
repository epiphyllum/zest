package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dao.JBatchDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JBatchEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.service.JAuthService;
import io.renren.zcommon.ZestConstant;
import io.renren.zin.cardtxn.ZinCardTxnService;
import io.renren.zin.cardtxn.dto.TAuthQuery;
import io.renren.zin.cardtxn.dto.TAuthResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JBatchAuth extends  JBatchBase {

    @Resource
    private ZinCardTxnService zinCardTxnService;
    @Resource
    private JBatchDao jBatchDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JAuthService jAuthService;
    @Resource
    private JAuthDao jAuthDao;
    @Resource
    private TransactionTemplate tx;

    // 授权流水下载
    public void authBatch(String dateStr) {
        // 创建任务记录
        log.info("创建任务: {}, {}", dateStr, ZestConstant.BATCH_TYPE_AUTH);
        JBatchEntity batchEntity = newBatchItem(dateStr, ZestConstant.BATCH_TYPE_AUTH);

        TAuthQuery tAuthQuery = new TAuthQuery();
        tAuthQuery.setPageindex(1);
        tAuthQuery.setPagesize(100);
        tAuthQuery.setTrxdate(dateStr);

        // 第一次下载
        TAuthResponse tAuthResponse = zinCardTxnService.authQuery(tAuthQuery);
        Integer total = tAuthResponse.getTotal();

        // 如果记录数为0
        if (total == 0) {
            jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                    .eq(JBatchEntity::getId, batchEntity.getId())
                    .set(JBatchEntity::getState, ZestConstant.BATCH_STATUS_SUCCESS)
                    .set(JBatchEntity::getMemo, "记录:" + total));
            return;
        }

        int downloaded = tAuthResponse.getDatalist().size();
        int batchSize = 1000;
        int batchCount = total / 1000 + 1;

        // 所有流水
        List<List<JAuthEntity>> batchList = new ArrayList<>(batchCount);

        // 准备批次
        List<JAuthEntity> curBatch = new ArrayList<>(1000);

        // 第一次填充数据
        for (TAuthResponse.Item item : tAuthResponse.getDatalist()) {
            JAuthEntity entity = ConvertUtils.sourceToTarget(item, JAuthEntity.class);
            // 补充卡信息
            JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, entity.getCardno())
            );
            entity.setWalletId(cardEntity.getWalletId());
//            entity.setWalletName(cardEntity.getWalletName());
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
            tAuthQuery.setPageindex(tAuthQuery.getPageindex() + 1);
            tAuthResponse = zinCardTxnService.authQuery(tAuthQuery);
            downloaded += tAuthResponse.getDatalist().size();
            for (TAuthResponse.Item item : tAuthResponse.getDatalist()) {
                JAuthEntity entity = ConvertUtils.sourceToTarget(item, JAuthEntity.class);
                // 补充卡信息
                JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                        .eq(JCardEntity::getCardno, entity.getCardno())
                );
                entity.setWalletId(cardEntity.getWalletId());
//                entity.setWalletName(cardEntity.getWalletName());
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
        }
        if (curBatch.size() < batchSize) {
            batchList.add(curBatch);
        }
        log.info("total auth: {}", total);
        boolean success = true;

        // 开始插入批次
        try {
            int i = 0;
            for (List<JAuthEntity> batch : batchList) {
                log.info("process batch-{}: {}", i, batch.size());
                tx.executeWithoutResult(st -> {
                    try {
                        jAuthService.insertBatch(batch);
                        log.info("insert batch success");
                    } catch (Exception ex) {
                        int newAdd = 0;
                        for (JAuthEntity entity : batch) {
                            try {
                                jAuthDao.insert(entity);
                                newAdd++;
                            } catch (DuplicateKeyException e) {
                                // 插入有重复: 说明是已经同步过的
                            }
                        }
                        log.info("newly add: {}", newAdd);
                        return;
                    }
                });
                log.info("process batch-{} complete", i);
                i++;
            }
        } catch (Exception ex) {
            log.error("process failed, ex = {}", ex.getMessage());
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
