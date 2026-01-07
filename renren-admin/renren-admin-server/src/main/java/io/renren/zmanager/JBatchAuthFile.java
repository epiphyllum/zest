package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dao.JBatchDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAuthEntity;
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
public class JBatchAuthFile extends JBatchBase {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JAuthDao jAuthDao;
    @Resource
    private JBatchDao jBatchDao;

    // 生成某个商户的文件
    private void genAuthFile(Long merchantId, String entrydate) throws IOException {
        // 传入时间: 2026-01-01
        // trxtime:  2026-01-01 10:10:10
        //String dbEntryDate = entrydate.replaceAll("-", "");

        List<JAuthEntity> jAuthEntities = jAuthDao.selectList(Wrappers.<JAuthEntity>lambdaQuery()
                .eq(JAuthEntity::getMerchantId, merchantId)
                .likeRight(JAuthEntity::getTrxtime, entrydate)
        );


        log.info("商户:{}, 交易日期:{}, 记录数: {}", merchantId, entrydate, jAuthEntities.size());

        List<String> lines = new ArrayList<>(jAuthEntities.size());
        StringBuilder sb = new StringBuilder();

        // 表头
        sb.append("子商户号,子商户名,卡产品类型,主卡卡号,");  // 4
        sb.append("卡ID,子卡卡号,流水号,交易类型,交易方向,"); // 5
        sb.append("状态,金额,币种,清算金额,清算币种,"); // 5
        sb.append("交易时间,MCC,交易地址,受理国家,"); // 4
        sb.append("应答码,应答消息,3DS标志,授权码,"); // 4
        lines.add(sb.toString());

        // 文件体
        jAuthEntities.forEach(jAuthEntity -> {
            StringBuilder lineSB = new StringBuilder();

            lineSB.append(jAuthEntity.getSubId()).append(",")
                    .append(jAuthEntity.getSubName()).append(",")
                    .append(jAuthEntity.getMarketproduct()).append(",")
                    .append(jAuthEntity.getMaincardno()).append(",")

                    .append(jAuthEntity.getCardid()).append(",")
                    .append(jAuthEntity.getCardno()).append(",")
                    .append(jAuthEntity.getLogkv()).append(",")
                    .append(jAuthEntity.getTrxcode()).append(",")
                    .append(jAuthEntity.getTrxdir()).append(",")

                    .append(jAuthEntity.getState()).append(",")
                    .append(jAuthEntity.getAmount()).append(",")
                    .append(jAuthEntity.getCurrency()).append(",")
                    .append(jAuthEntity.getSettleamount()).append(",")
                    .append(jAuthEntity.getSettlecurrency()).append(",")

                    .append(jAuthEntity.getTrxtime()).append(",")
                    .append(jAuthEntity.getMcc()).append(",")
                    .append(jAuthEntity.getTrxaddr().replace(',', ' ')).append(",")
                    .append(jAuthEntity.getAcqcountry()).append(",")


                    .append(jAuthEntity.getRespcode()).append(",")
                    .append(jAuthEntity.getRespmsg()).append(",")
                    .append(jAuthEntity.getDsflag()).append(",")
                    .append(jAuthEntity.getAuthcode()).append(",");
            String line = lineSB.toString();
            lines.add(line);
        });
        /**
         *     // 业务字段
         *     private String logkv;
         *     private String trxcode;
         *     private String cardid;
         *     private String cardno;
         *     private String state;
         *     private String stateexplain;
         *     private String respmsg;
         *     private String respcode;
         *
         *     private BigDecimal amount;
         *     private String currency;
         *
         *     private BigDecimal settleamount;
         *     private String settlecurrency;
         *
         *     private String trxtime;
         *     private String trxdir;
         *     private String trxaddr;
         *     private String authcode;
         *     private String mcc;
         *     private String time;
         *
         *     private String acqcountry;// todo 3
         *     private String dsflag;  // todo  1
         *
         */

        String filename = zestConfig.getUploadDir() + "/auth/" + merchantId + "/" + entrydate + ".txt";
        log.info("生成授权交易文件: {}, {} -> {}", merchantId, entrydate, filename);

        File file = new File(filename);
        FileUtils.forceMkdirParent(file); // 创建父目录
        FileUtils.writeLines(file, lines);
    }

    // 按商户生成结算流水文件
    public void authFileBatch(String dateStr) {
        // 结算文件下载任务是否完成
        JBatchEntity authedFetched = jBatchDao.selectOne(Wrappers.<JBatchEntity>lambdaQuery()
                .eq(JBatchEntity::getBatchDate, dateStr)
                .eq(JBatchEntity::getBatchType, ZestConstant.BATCH_TYPE_AUTH)
        );
        if (authedFetched == null) {
            throw new RenException(dateStr + ",授权流水尚未从通联下载");
        }
        // 创建任务记录
        log.info("创建任务: {}, {}", dateStr, ZestConstant.BATCH_TYPE_AUTH_FILE);
        JBatchEntity batchEntity = newBatchItem(dateStr, ZestConstant.BATCH_TYPE_AUTH_FILE);

        List<Long> ids = jMerchantDao.selectList(Wrappers.<JMerchantEntity>lambdaQuery()
                .select(JMerchantEntity::getId)
        ).stream().map(JMerchantEntity::getId).toList();

        for (Long id : ids) {
            try {
                genAuthFile(id, dateStr);
            } catch (IOException e) {
                log.error("生成文件失败 - 商户ID: {}", id);
                jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                        .eq(JBatchEntity::getId, batchEntity.getId())
                        .eq(JBatchEntity::getState, batchEntity.getState())
                        .set(JBatchEntity::getState, ZestConstant.BATCH_STATUS_FAIL));
                throw new RenException("生成授权交易文件失败:" + id);
            }
        }

        // 生成失败
        jBatchDao.update(null, Wrappers.<JBatchEntity>lambdaUpdate()
                .eq(JBatchEntity::getId, batchEntity.getId())
                .eq(JBatchEntity::getState, batchEntity.getState())
                .set(JBatchEntity::getState, ZestConstant.BATCH_STATUS_SUCCESS));

    }
}
