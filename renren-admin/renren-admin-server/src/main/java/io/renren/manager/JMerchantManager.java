package io.renren.manager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zin.service.file.ZinFileService;
import io.renren.zin.service.sub.ZinSubService;
import io.renren.zin.service.sub.dto.TSubCreateRequest;
import io.renren.zin.service.sub.dto.TSubCreateResponse;
import io.renren.zin.service.sub.dto.TSubQuery;
import io.renren.zin.service.sub.dto.TSubQueryResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JMerchantManager {

    @Resource
    private JMerchantDao jMerchantDao;

    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ZinSubService zinSubService;

    public void submit(JMerchantEntity jMerchantEntity) {
        // 上传文件
        this.uploadFiles(jMerchantEntity);

        // 准备请求
        TSubCreateRequest tSubCreateRequest = ConvertUtils.sourceToTarget(jMerchantEntity, TSubCreateRequest.class);
        tSubCreateRequest.setMeraplid(jMerchantEntity.getId().toString());

        // 调用通联
        TSubCreateResponse response = zinSubService.create(tSubCreateRequest);

        // 更新应答
        String cusid = response.getCusid();
        jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                .eq(JMerchantEntity::getId, jMerchantEntity.getId())
                .set(JMerchantEntity::getCusid, cusid)
                .set(JMerchantEntity::getMeraplid, tSubCreateRequest.getMeraplid())
        );
    }

    private void uploadFiles(JMerchantEntity jMerchantEntity) {
        // 拿到所有文件fid
        String agreementfid = jMerchantEntity.getAgreementfid();
        String buslicensefid = jMerchantEntity.getBuslicensefid();
        String creditfid = jMerchantEntity.getCreditfid();
        String legalphotobackfid = jMerchantEntity.getLegalphotobackfid();
        String legalphotofrontfid = jMerchantEntity.getLegalphotofrontfid();
        String taxfid = jMerchantEntity.getTaxfid();
        String organfid = jMerchantEntity.getOrganfid();

        List<String> fids = List.of(agreementfid, buslicensefid, creditfid, legalphotobackfid, legalphotofrontfid, taxfid, organfid);
        Map<String, CompletableFuture<String>> jobs = new HashMap<>();
        for (String fid : fids) {
            if (StringUtils.isBlank(fid)) {
                continue;
            }
            jobs.put(fid, CompletableFuture.supplyAsync(() -> {
                return zinFileService.upload(fid);
            }));
        }
        jobs.forEach((j, f) -> {
            log.info("wait {}...", j);
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RenException("can not upload file:" + j);
            }
        });
        log.info("文件上传完毕, 开始请求创建商户...");
    }

    public void query(JMerchantEntity jMerchantEntity) {
        TSubQuery tSubQuery = ConvertUtils.sourceToTarget(jMerchantEntity, TSubQuery.class);
        TSubQueryResponse response = zinSubService.query(tSubQuery);
        jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                .eq(JMerchantEntity::getId, jMerchantEntity.getId())
                .set(JMerchantEntity::getState, response.getState())
                .set(JMerchantEntity::getCusid, response.getCusid())
        );
    }
}
