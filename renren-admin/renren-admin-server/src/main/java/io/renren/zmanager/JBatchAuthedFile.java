package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.DateUtils;
import io.renren.zadmin.dao.JAuthedDao;
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
import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    // 生成某个商户的文件
    private void genAuthedFile(Long merchantId, String entrydate) {
        List<JAuthedEntity> jAuthedEntities = jAuthedDao.selectList(Wrappers.<JAuthedEntity>lambdaQuery()
                .eq(JAuthedEntity::getMerchantId, merchantId)
                .eq(JAuthedEntity::getEntrydate, entrydate)
        );

        StringBuilder sb = new StringBuilder();
        jAuthedEntities.forEach(jAuthedEntity -> {
            sb.append("");
        });

        try {
            String filename = zestConfig.getUploadDir() + "/settle/" + merchantId + "/" + entrydate + ".txt";
            File file = new File(filename);
            FileUtils.writeStringToFile(file, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RenException("can not save");
        }
    }

    // 按商户生成结算流水文件
    public void authedFileBatch(String dateStr) {
        Date date = DateUtils.parse(dateStr, DateUtils.DATE_PATTERN);
        // 创建任务记录
        JBatchEntity batchEntity = newBatchItem(dateStr, ZestConstant.BATCH_TYPE_AUTHED);

        List<Long> ids = jMerchantDao.selectList(Wrappers.<JMerchantEntity>lambdaQuery()
                .select(JMerchantEntity::getId)
        ).stream().map(JMerchantEntity::getId).toList();
        for (Long id : ids) {
            genAuthedFile(id, dateStr);
        }
    }
}
