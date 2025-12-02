package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JAuthedDao;
import io.renren.zadmin.dao.JBatchDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAuthedEntity;
import io.renren.zadmin.entity.JBatchEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZestConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JBatchAuthedFile extends JBatchBase {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JAuthedDao jAuthedDao;
    @Resource
    private JBatchDao jBatchDao;

    // 生成某个商户的文件
    private void genAuthedFile(Long merchantId, String entrydate) throws IOException {
        String dbEntryDate = entrydate.replaceAll("-", "");
        List<JAuthedEntity> jAuthedEntities = jAuthedDao.selectList(Wrappers.<JAuthedEntity>lambdaQuery()
                .eq(JAuthedEntity::getMerchantId, merchantId)
                .eq(JAuthedEntity::getEntrydate, dbEntryDate)
        );


        log.info("商户:{}, 入账日期:{}, 记录数: {}", merchantId, entrydate, jAuthedEntities.size());

        List<String> lines = new ArrayList<>(jAuthedEntities.size());
        StringBuilder sb = new StringBuilder();

        // 表头
        sb.append("子商户号,子商户名,卡产品类型,主卡卡号,子卡卡号,交易类型,交易方向,交易状态,交易金额,交易币种,入账金额,入账币种,交易时间,入账日期,入账流水号,受理机构地址,授权码,流水号,商户类别");
        lines.add(sb.toString());

        // 文件体
        jAuthedEntities.forEach(jAuthedEntity -> {
            StringBuilder lineSB = new StringBuilder();
            lineSB.append(jAuthedEntity.getSubId()).append(",")
                    .append(jAuthedEntity.getSubName()).append(",")
                    .append(jAuthedEntity.getMarketproduct()).append(",")
                    .append(jAuthedEntity.getMaincardno()).append(",")
                    .append(jAuthedEntity.getCardno()).append(",")
                    .append(jAuthedEntity.getTrxcode()).append(",")
                    .append(jAuthedEntity.getTrxdir()).append(",")
                    .append(jAuthedEntity.getState()).append(",")
                    .append(jAuthedEntity.getAmount()).append(",")
                    .append(jAuthedEntity.getCurrency()).append(",")
                    .append(jAuthedEntity.getEntryamount()).append(",")
                    .append(jAuthedEntity.getEntrycurrency()).append(",")
                    .append(jAuthedEntity.getTrxtime()).append(",")
                    .append(jAuthedEntity.getEntrydate()).append(",")
                    .append(jAuthedEntity.getChnltrxseq()).append(",")
                    .append(jAuthedEntity.getTrxaddr().replace(',', ' ')).append(",")
                    .append(jAuthedEntity.getAuthcode()).append(",")
                    .append(jAuthedEntity.getLogkv()).append(",")
                    .append(jAuthedEntity.getMcc());
            String line = lineSB.toString();
            lines.add(line);
        });

        String filename = zestConfig.getUploadDir() + "/settle/" + merchantId + "/" + entrydate + ".txt";
        log.info("生成结算文件: {}, {} -> {}", merchantId, entrydate, filename);

        File file = new File(filename);
        FileUtils.forceMkdirParent(file); // 创建父目录
        FileUtils.writeLines(file, lines);
    }

    // 按商户生成结算流水文件
    public void authedFileBatch(String dateStr) {
        // 结算文件下载任务是否完成
        JBatchEntity authedFetched = jBatchDao.selectOne(Wrappers.<JBatchEntity>lambdaQuery()
                .eq(JBatchEntity::getBatchDate, dateStr)
                .eq(JBatchEntity::getBatchType, ZestConstant.BATCH_TYPE_AUTHED)
        );
        if (authedFetched == null) {
            throw new RenException(dateStr + ",结算文件尚未下载");
        }
        // 创建任务记录
        log.info("创建任务: {}, {}", dateStr, ZestConstant.BATCH_TYPE_AUTHED_FILE);
        JBatchEntity batchEntity = newBatchItem(dateStr, ZestConstant.BATCH_TYPE_AUTHED_FILE);

        List<Long> ids = jMerchantDao.selectList(Wrappers.<JMerchantEntity>lambdaQuery()
                .select(JMerchantEntity::getId)
        ).stream().map(JMerchantEntity::getId).toList();

        for (Long id : ids) {
            try {
                genAuthedFile(id, dateStr);
            } catch (IOException e) {
                log.error("生成文件失败 - 商户ID: {}", id);
                jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                        .eq(JBatchEntity::getId, batchEntity.getId())
                        .eq(JBatchEntity::getState, batchEntity.getState())
                        .set(JBatchEntity::getState, ZestConstant.BATCH_STATUS_FAIL));
                throw new RenException("生成商户结算文件失败:" + id);
            }
        }

        // 生成失败
        jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                .eq(JBatchEntity::getId, batchEntity.getId())
                .eq(JBatchEntity::getState, batchEntity.getState())
                .set(JBatchEntity::getState, ZestConstant.BATCH_STATUS_SUCCESS));

    }
}
