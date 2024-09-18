package io.renren.zin.service.sub;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zbalance.Ledger;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.file.ZinFileService;
import io.renren.zin.service.sub.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
@Slf4j
public class ZinSubService {
    @Resource
    private ZinRequester requester;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinFileService zinFileService;

    private void uploadFiles() {

    }

    // 创建通联子商户: 5000
    public void create(Long id) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(id);

        // 上传文件
        uploadFiles(jMerchantEntity);

        String requestId = newRequestId();
        TSubCreateRequest tSubCreateRequest = ConvertUtils.sourceToTarget(jMerchantEntity, TSubCreateRequest.class);
        tSubCreateRequest.setMeraplid(requestId);

        TSubCreateResponse response = requester.request(newRequestId(), "/gcpapi/card/mermanage/create", tSubCreateRequest, TSubCreateResponse.class);
        String cusid = response.getCusid();
        jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                .eq(JMerchantEntity::getId, id)
                .set(JMerchantEntity::getCusid, cusid)
                .set(JMerchantEntity::getMeraplid, requestId)
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

    // 查询通联子商户创建情况: 5001
    public void query(Long id) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(id);
        String requestId = newRequestId();
        TSubQuery tSubQuery = ConvertUtils.sourceToTarget(jMerchantEntity, TSubQuery.class);
        TSubQueryResponse response = requester.request(requestId, "/gcpapi/card/mermanage/detail", tSubQuery, TSubQueryResponse.class);
        jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                .eq(JMerchantEntity::getId, jMerchantEntity.getId())
                .set(JMerchantEntity::getState, response.getState())
        );
    }

    // 子商户创建审核通知: 5002
    public void merchantStatusNotify(TSubStatusNotify notify) {
        if (notify.getState().equals("04") || notify.getState().equals("05")) {
            JMerchantEntity jMerchantEntity = jMerchantDao.selectOne(Wrappers.<JMerchantEntity>lambdaQuery()
                    .eq(JMerchantEntity::getCusid, notify.getCusid())
            );
            jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                    .set(JMerchantEntity::getState, notify.getState())
                    .eq(JMerchantEntity::getId, jMerchantEntity.getId())
            );
        }
    }

}
