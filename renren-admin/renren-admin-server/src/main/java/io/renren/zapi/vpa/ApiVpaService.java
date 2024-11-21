package io.renren.zapi.vpa;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.vpa.dto.*;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zmanager.JVpaManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiVpaService {

    @Resource
    private JVpaManager jVpaManager;
    @Resource
    private JVpaJobDao jVpaJobDao;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private ZestConfig zestConfig;

    // 发行预付费子卡
    public Result<NewPrepaidJobRes> newPrepaidJob(NewPrepaidJobReq request, ApiContext context) {
        JVpaJobEntity entity = ConvertUtils.sourceToTarget(request, JVpaJobEntity.class);
        jVpaManager.save(entity);
        jVpaManager.submit(entity);
        NewPrepaidJobRes res = new NewPrepaidJobRes();
        Result<NewPrepaidJobRes> result = new Result<>();
        result.setData(res);
        return result;
    }

    // 发行共享子卡
    public Result<NewShareJobRes> newShareJob(NewShareJobReq request, ApiContext context) {
        JVpaJobEntity entity = ConvertUtils.sourceToTarget(request, JVpaJobEntity.class);
        jVpaManager.save(entity);
        jVpaManager.submit(entity);
        NewShareJobRes res = new NewShareJobRes();
        Result<NewShareJobRes> result = new Result<>();
        result.setData(res);
        return result;
    }

    // 发行结果
    public Result<VpaJobQueryRes> vpaJobQuery(VpaJobQuery request, ApiContext context) {
        if (request.getApplyid() == null && request.getMeraplid() == null) {
            throw new RenException("字段applyid,meraplyid至少提供一个");
        }

        JVpaJobEntity job = jVpaJobDao.selectOne(Wrappers.<JVpaJobEntity>lambdaQuery()
                .eq(request.getApplyid() != null, JVpaJobEntity::getApplyid, request.getApplyid())
                .eq(request.getMeraplid() != null, JVpaJobEntity::getMeraplid, request.getMeraplid())
        );
        if (job == null) {
            throw new RenException("记录不存在");
        }

        // todo: 状态
        Result<VpaJobQueryRes> result = new Result<>();
        String vpaResult = this.getVpaResult(job);
        VpaJobQueryRes res = new VpaJobQueryRes();
        res.setEncrypted(vpaResult);
        res.setState("01");
        return result;
    }

    private String getVpaResult(JVpaJobEntity entity) {
        Long merchantId = entity.getMerchantId();
        List<JobItem> list = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getVpaJob, entity.getId())
                .select(
                        JCardEntity::getCardno,
                        JCardEntity::getCvv,
                        JCardEntity::getExpiredate
                )
        ).parallelStream().map(e -> {
            String plainCvv = CommonUtils.decryptSensitiveString(e.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "utf-8");
            String plainDate = e.getExpiredate();
            JobItem jobItem = new JobItem();
            jobItem.setCardno(e.getCardno());
            jobItem.setCvv(plainCvv);
            jobItem.setExpiredate(plainDate);
            return jobItem;
        }).toList();
        String sensitiveInfo = null;
        try {
            JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
            sensitiveInfo = objectMapper.writeValueAsString(list);
            return CommonUtils.encryptSensitiveString(sensitiveInfo, merchant.getSensitiveKey(), "utf-8");
        } catch (JsonProcessingException e) {
            throw new RenException("加密失败");
        }
    }

}
