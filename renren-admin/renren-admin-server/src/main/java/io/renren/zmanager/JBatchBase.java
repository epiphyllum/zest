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

@Slf4j
public class JBatchBase {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JBatchDao jBatchDao;
    @Resource
    private JMoneyDao jMoneyDao;
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

    /**
     * 插入任务
     */
    protected JBatchEntity newBatchItem(Date date, String batchType) {
        JBatchEntity batchEntity = jBatchDao.selectOne(Wrappers.<JBatchEntity>lambdaQuery()
                .eq(JBatchEntity::getBatchDate, date)
                .eq(JBatchEntity::getBatchType, batchType)
        );
        if (batchEntity == null) {
            log.info("no batch entity, create new one... {}, {}", batchType, date);
            batchEntity = new JBatchEntity();
            batchEntity.setState(ZestConstant.BATCH_STATUS_NEW);
            batchEntity.setBatchDate(date);
            batchEntity.setBatchType(batchType);
            jBatchDao.insert(batchEntity);
        }
        return batchEntity;
    }

}
