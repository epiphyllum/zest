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
import io.renren.zcommon.ZestConstant;
import io.renren.zin.cardtxn.ZinCardTxnService;
import io.renren.zin.cardtxn.dto.TAuthSettledQuery;
import io.renren.zin.cardtxn.dto.TAuthSettledResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class JBatchManager {
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JBatchDao jBatchDao;
    @Resource
    private JStatDao jStatDao;
    @Resource
    private JStatService jStatService;
    @Resource
    private VWithdrawDao vWithdrawDao;
    @Resource
    private VDepositDao vDepositDao;
    @Resource
    private VCardDao vCardDao;
    @Resource
    private JDepositDao jDepositDao;
    @Resource
    private JWithdrawDao jWithdrawDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JAuthedDao jAuthedDao;
    @Resource
    private JAuthedService jAuthedService;
    @Resource
    private ZinCardTxnService zinCardTxnService;

    private void checkStatDate(Date date) {
        Date next = new Date(date.getTime() + 1000 * 60 * 60 * 24);

        Long depositProcess = jDepositDao.selectCount(Wrappers.<JDepositEntity>lambdaQuery()
                .eq(JDepositEntity::getState, "")
                .ge(JDepositEntity::getCreateDate, date)
                .lt(JDepositEntity::getCreateDate, next)
        );
        if (depositProcess > 0) {
            throw new RenException("充值交易:" + depositProcess + "比处理中");
        }
        Long withdrawProcess = jWithdrawDao.selectCount(Wrappers.<JWithdrawEntity>lambdaQuery()
                .eq(JWithdrawEntity::getState, "")
                .ge(JWithdrawEntity::getCreateDate, date)
                .lt(JWithdrawEntity::getCreateDate, next)
        );
        if (withdrawProcess > 0) {
            throw new RenException("提现交易:" + withdrawProcess + "比处理中");
        }
        Long cardProcess = jCardDao.selectCount(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getState, "")
                .ge(JCardEntity::getCreateDate, date)
                .lt(JCardEntity::getCreateDate, next)
        );
        if (cardProcess > 0) {
            throw new RenException("发卡交易:" + cardProcess + "比处理中");
        }
    }

    // 结算流水下载
    public void statAuthed(String dateStr) {
        Date date = DateUtils.parse(dateStr, DateUtils.DATE_PATTERN);
        JBatchEntity batchEntity = newBatchItem(date, ZestConstant.BATCH_TYPE_AUTHED);

        // 第一次下载
        String entryDate = dateStr.replaceAll("-", "");
        TAuthSettledQuery request = new TAuthSettledQuery();
        request.setPagesize(100);
        request.setPagesize(1);
        request.setEntrydate(entryDate);
        TAuthSettledResponse response = zinCardTxnService.settledQuery(request);
        int downloaded = response.getDatalist().size();
        int total = response.getTotal();
        int batchSize = 1000;
        int batchCount = total / 1000 + 1;

        // 批次
        List<List<JAuthedEntity>> batchList = new ArrayList<>(batchCount);

        //  准备批次
        List<JAuthedEntity> curBatch = new ArrayList<>(1000);
        for (TAuthSettledResponse.Item item : response.getDatalist()) {
            JAuthedEntity entity = ConvertUtils.sourceToTarget(item, JAuthedEntity.class);
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

        // 开始插入批次
        for (List<JAuthedEntity> batch : batchList) {
            tx.executeWithoutResult(st -> {
                try {
                    jAuthedService.insertBatch(batch);
                } catch (Exception ex) {
                    for (JAuthedEntity jAuthedEntity : batch) {
                        try {
                            jAuthedDao.insert(jAuthedEntity);
                        } catch (DuplicateKeyException e) {
                        }
                    }
                }
            });
        }

        jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                .eq(JBatchEntity::getId, batchEntity.getId())
                .eq(JBatchEntity::getState, batchEntity.getState())
                .set(JBatchEntity::getState, ZestConstant.BATCH_STATUS_SUCCESS)
                .set(JBatchEntity::getMemo, "记录:" + total));

    }

    private JBatchEntity newBatchItem(Date date, String batchType) {
        JBatchEntity batchEntity = jBatchDao.selectOne(Wrappers.<JBatchEntity>lambdaQuery()
                .eq(JBatchEntity::getBatchDate, date)
                .eq(JBatchEntity::getBatchType, batchType)
        );
        if (batchEntity == null) {
            batchEntity = new JBatchEntity();
            batchEntity.setState(ZestConstant.BATCH_STATUS_NEW);
            batchEntity.setBatchDate(date);
            batchEntity.setBatchType(batchType);
            jBatchDao.insert(batchEntity);
        }
        return batchEntity;
    }

    // 生成某一天的数据
    public void statBatch(String dateStr) {
        Date date = DateUtils.parse(dateStr, DateUtils.DATE_PATTERN);

        // 检查是否可以统计
        checkStatDate(date);

        // 拿到任务
        JBatchEntity batchEntity = newBatchItem(date, ZestConstant.BATCH_TYPE_STAT);
        if (batchEntity.getState().equals(ZestConstant.BATCH_STATUS_SUCCESS)) {
            throw new RenException("重复执行");
        }

        Map<String, VCardEntity> cardMap = new HashMap<>();
        Map<String, VDepositEntity> depositMap = new HashMap<>();
        Map<String, VWithdrawEntity> withdrawMap = new HashMap<>();
        Set<String> allKeys = new HashSet<>();

        // 卡统计
        List<VCardEntity> cardList = vCardDao.selectByDate(date);
        for (VCardEntity vCardEntity : cardList) {
            String key = vCardEntity.getAgentName() + vCardEntity.getAgentId()
                    + vCardEntity.getMerchantName() + vCardEntity.getMerchantId()
                    + vCardEntity.getSubName() + vCardEntity.getSubId()
                    + vCardEntity.getMarketproduct()
                    + vCardEntity.getCurrency();
            cardMap.put(key, vCardEntity);
            allKeys.add(key);
        }

        // 充值统计
        List<VDepositEntity> vDepositEntities = vDepositDao.selectByDate(date);
        for (VDepositEntity vDepositEntity : vDepositEntities) {
            String key = vDepositEntity.getAgentName() + vDepositEntity.getAgentId()
                    + vDepositEntity.getMerchantName() + vDepositEntity.getMerchantId()
                    + vDepositEntity.getSubName() + vDepositEntity.getSubId()
                    + vDepositEntity.getMarketproduct()
                    + vDepositEntity.getCurrency();
            depositMap.put(key, vDepositEntity);
            allKeys.add(key);
        }

        // 提现统计
        List<VWithdrawEntity> vWithdrawEntities = vWithdrawDao.selectByDate(date);
        for (VWithdrawEntity vWithdrawEntity : vWithdrawEntities) {
            String key = vWithdrawEntity.getAgentName() + vWithdrawEntity.getAgentId()
                    + vWithdrawEntity.getMerchantName() + vWithdrawEntity.getMerchantId()
                    + vWithdrawEntity.getSubName() + vWithdrawEntity.getSubId()
                    + vWithdrawEntity.getMarketproduct()
                    + vWithdrawEntity.getCurrency();
            withdrawMap.put(key, vWithdrawEntity);
            allKeys.add(key);
        }

        int batchSize = 1000;

        List<List<JStatEntity>> batchList = new ArrayList<>();
        List<JStatEntity> curBatch = new ArrayList<>(batchSize);

        int total = 0;

        // 合并:
        for (String key : allKeys) {
            VWithdrawEntity withdrawEntity = withdrawMap.get(key);
            VCardEntity cardEntity = cardMap.get(key);
            VDepositEntity depositEntity = depositMap.get(key);
            JStatEntity statEntity = new JStatEntity();
            String md5Key = DigestUtil.md5Hex(key + dateStr);
            statEntity.setMd5(md5Key);
            statEntity.setStatDate(date);
            setFields(withdrawEntity, cardEntity, depositEntity, statEntity);
            curBatch.add(statEntity);
            if (curBatch.size() == batchSize) {
                batchList.add(curBatch);
                curBatch = new ArrayList<>(batchSize);
            }
            total += 1;
        }

        if (curBatch.size() < batchSize) {
            batchList.add(curBatch);
        }

        String memo = String.format("合并统计, 充值交易:%d, 提现交易:%d, 发卡交易:%d, 合并:%d, 批次:%d",
                vDepositEntities.size(), vWithdrawEntities.size(), cardList.size(), total, batchList.size()
        );
        System.out.println(memo);

        boolean success = true;
        try {
            for (List<JStatEntity> batch : batchList) {
                tx.executeWithoutResult(st -> {
                    try {
                        jStatService.insertBatch(batch);
                    } catch (DuplicateKeyException ex) {
                        log.error("batch failed: {}", ex.getMessage());
                        for (JStatEntity jStatEntity : batch) {
                            try {
                                jStatDao.insert(jStatEntity);
                            } catch (DuplicateKeyException e) {
                                log.error("insert failed: {}", e.getMessage());
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        String updateState = success ? ZestConstant.BATCH_STATUS_SUCCESS : ZestConstant.BATCH_STATUS_FAIL;
        jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                .eq(JBatchEntity::getId, batchEntity.getId())
                .eq(JBatchEntity::getState, batchEntity.getState())
                .set(JBatchEntity::getState, updateState)
                .set(JBatchEntity::getMemo, memo));

    }

    private void setFields(VWithdrawEntity withdrawEntity, VCardEntity cardEntity, VDepositEntity depositEntity, JStatEntity statEntity) {
        boolean isKeySet = false;
        if (depositEntity != null) {
            if (!isKeySet) {
                statEntity.setAgentName(depositEntity.getAgentName());
                statEntity.setMerchantName(depositEntity.getAgentName());
                statEntity.setSubName(depositEntity.getAgentName());
                statEntity.setAgentId(depositEntity.getAgentId());
                statEntity.setMerchantId(depositEntity.getMerchantId());
                statEntity.setSubId(depositEntity.getSubId());
                statEntity.setMarketproduct(depositEntity.getMarketproduct());
                statEntity.setCurrency(depositEntity.getCurrency());
                isKeySet = true;
            }
            statEntity.setCardSum(depositEntity.getCardSum());
            statEntity.setAipCardSum(depositEntity.getAipCardSum());
            statEntity.setCharge(depositEntity.getCharge());
            statEntity.setAipCharge(depositEntity.getAipCharge());
            statEntity.setDeposit(depositEntity.getDeposit());
            statEntity.setAipDeposit(depositEntity.getAipDeposit());
        } else {
            statEntity.setCardSum(BigDecimal.ZERO);
            statEntity.setAipCardSum(BigDecimal.ZERO);
            statEntity.setCharge(BigDecimal.ZERO);
            statEntity.setAipCharge(BigDecimal.ZERO);
            statEntity.setDeposit(BigDecimal.ZERO);
            statEntity.setAipDeposit(BigDecimal.ZERO);
        }
        if (cardEntity != null) {
            if (!isKeySet) {
                statEntity.setAgentName(cardEntity.getAgentName());
                statEntity.setMerchantName(cardEntity.getAgentName());
                statEntity.setSubName(cardEntity.getAgentName());
                statEntity.setAgentId(cardEntity.getAgentId());
                statEntity.setMerchantId(cardEntity.getMerchantId());
                statEntity.setSubId(cardEntity.getSubId());
                statEntity.setMarketproduct(cardEntity.getMarketproduct());
                statEntity.setCurrency(cardEntity.getCurrency());
            }
            statEntity.setCardFee(cardEntity.getMerchantfee());
            statEntity.setAipCardFee(cardEntity.getFee());
            statEntity.setTotalCard(cardEntity.getTotalCard());
        } else {
            statEntity.setCardFee(BigDecimal.ZERO);
            statEntity.setAipCardFee(BigDecimal.ZERO);
            statEntity.setTotalCard(0L);
        }
        if (withdrawEntity != null) {
            if (isKeySet) {
                statEntity.setAgentName(withdrawEntity.getAgentName());
                statEntity.setMerchantName(withdrawEntity.getAgentName());
                statEntity.setSubName(withdrawEntity.getAgentName());
                statEntity.setAgentId(withdrawEntity.getAgentId());
                statEntity.setMerchantId(withdrawEntity.getMerchantId());
                statEntity.setSubId(withdrawEntity.getSubId());
                statEntity.setMarketproduct(withdrawEntity.getMarketproduct());
                statEntity.setCurrency(withdrawEntity.getCurrency());
                isKeySet = true;
            }
            statEntity.setWithdraw(withdrawEntity.getCardSum());
            statEntity.setWithdrawCharge(withdrawEntity.getCharge());
            statEntity.setAipWithdrawCharge(withdrawEntity.getAipCharge());
        } else {
            statEntity.setWithdraw(BigDecimal.ZERO);
            statEntity.setWithdrawCharge(BigDecimal.ZERO);
            statEntity.setAipWithdrawCharge(BigDecimal.ZERO);
        }
    }
}
