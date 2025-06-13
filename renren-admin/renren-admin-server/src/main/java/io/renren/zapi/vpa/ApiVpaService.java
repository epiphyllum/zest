package io.renren.zapi.vpa;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JVpaAdjustDao;
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.vpa.dto.*;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JCardManager;
import io.renren.zmanager.JVpaManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiVpaService {

    @Resource
    private JVpaManager jVpaManager;
    @Resource
    private JCardManager jCardManager;
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
    @Resource
    private JVpaAdjustDao jVpaAdjustDao;

    // 发行预付费子卡
    public Result<NewPrepaidJobRes> newPrepaidJob(NewPrepaidJobReq request, ApiContext context) {
        JVpaJobEntity entity = ConvertUtils.sourceToTarget(request, JVpaJobEntity.class);
        entity.setMarketproduct(ZinConstant.MP_VPA_PREPAID);
        entity.setCycle(ZinConstant.VPA_CYCLE_DEADLINE);  //钱包子卡一定是期限卡
        jVpaManager.save(entity);
        jVpaManager.submit(entity);
        NewPrepaidJobRes res = new NewPrepaidJobRes();
        res.setApplyid(entity.getApplyid());
        Result<NewPrepaidJobRes> result = new Result<>();
        result.setData(res);
        return result;
    }

    // 发行共享子卡
    public Result<NewShareJobRes> newShareJob(NewShareJobReq request, ApiContext context) {
        JVpaJobEntity entity = ConvertUtils.sourceToTarget(request, JVpaJobEntity.class);
        entity.setMarketproduct(ZinConstant.MP_VPA_SHARE);
        jVpaManager.save(entity);
        jVpaManager.submit(entity);
        NewShareJobRes res = new NewShareJobRes();
        res.setApplyid(entity.getApplyid());
        Result<NewShareJobRes> result = new Result<>();
        result.setData(res);
        return result;
    }


    //  设置共享子卡额度
    public Result<SetQuotaRes> setQuota(SetQuotaReq request, ApiContext context) {
        Result<SetQuotaRes> result = new Result<>();
        JCardEntity card = getCard(request.getCardid(), request.getCardno());
        jCardManager.setQuota(card, request.getAuthmaxamount(), request.getAuthmaxcount(), 1);
        return result;
    }

    // 预防费卡充值
    public Result<PrepaidChargeRes> prepaidCharge(PrepaidChargeReq request, ApiContext context) {
        Result<PrepaidChargeRes> result = new Result<>();
        JCardEntity card = getCard(request.getCardid(), request.getCardno());
        jCardManager.prepaidCharge(card, request.getAmount(), request.getMeraplid(), 1);
        return result;
    }

    // 预防费卡充值
    public Result<PrepaidChargeQueryRes> prepaidChargeQuery(PrepaidChargeQuery request, ApiContext context) {
        Result<PrepaidChargeQueryRes> result = new Result<>();
        JVpaAdjustEntity adjustEntity = jVpaAdjustDao.selectOne(Wrappers.<JVpaAdjustEntity>lambdaQuery()
                .eq(JVpaAdjustEntity::getMeraplid, request.getMeraplid())
        );

        if (adjustEntity == null) {
            throw new RenException("订单不存在:" + request.getMeraplid());
        }

        PrepaidChargeQueryRes res = new PrepaidChargeQueryRes();
        res.setMeraplid(request.getMeraplid());
        res.setState(adjustEntity.getState());
        res.setQuota(adjustEntity.getNewQuota());
        result.setData(res);
        return result;
    }

    // 预防费卡充值
    public Result<PrepaidWithdrawQueryRes> prepaidWithdrawQuery(PrepaidChargeQuery request, ApiContext context) {
        Result<PrepaidWithdrawQueryRes> result = new Result<>();
        JVpaAdjustEntity adjustEntity = jVpaAdjustDao.selectOne(Wrappers.<JVpaAdjustEntity>lambdaQuery()
                .eq(JVpaAdjustEntity::getMeraplid, request.getMeraplid())
        );
        if (adjustEntity == null) {
            throw new RenException("订单不存在:" + request.getMeraplid());
        }
        PrepaidWithdrawQueryRes res = new PrepaidWithdrawQueryRes();
        res.setMeraplid(request.getMeraplid());
        res.setState(adjustEntity.getState());
        res.setQuota(adjustEntity.getNewQuota());
        result.setData(res);
        return result;
    }

    // 预防费卡提现
    public Result<PrepaidWithdrawRes> prepaidWithdraw(PrepaidWithdrawReq request, ApiContext context) {
        Result<PrepaidWithdrawRes> result = new Result<>();
        jCardManager.prepaidWithdraw(getCard(request.getCardid(), request.getCardno()), request.getAmount(), request.getMeraplid(), 1);
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

        Result<VpaJobQueryRes> result = new Result<>();
        VpaJobQueryRes res = new VpaJobQueryRes();
        res.setState(job.getState());
        res.setApplyid(job.getApplyid());
        res.setMeraplid(job.getMeraplid());

        // 发卡成功
        if (!job.getState().equals(ZinConstant.CARD_APPLY_SUCCESS)) {
           if (job.getApplyid() != null) {
               // 再查一次
               jVpaManager.query(job, false);
           }
        }

        // 如果发卡成功
        if (job.getState().equals(ZinConstant.CARD_APPLY_SUCCESS)) {
            String vpaResult = this.getVpaResult(job);
            res.setEncrypted(vpaResult);
        }

        result.setData(res);
        return result;
    }

    // 发卡加密数据结果
    private String getVpaResult(JVpaJobEntity entity) {
        Long merchantId = entity.getMerchantId();
        List<JobItem> list = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getVpaJob, entity.getId())
                .select(
                        JCardEntity::getCardno,
                        JCardEntity::getCvv,
                        JCardEntity::getExpiredate,
                        JCardEntity::getCardid
                )
        ).parallelStream().map(e -> {
            String plainCvv = CommonUtils.decryptSensitiveString(e.getCvv(), zestConfig.getAccessConfig().getSensitiveKey(), "utf-8");
            String plainDate = e.getExpiredate();
            JobItem jobItem = new JobItem();
            jobItem.setCardno(e.getCardno());
            jobItem.setCvv(plainCvv);
            jobItem.setExpiredate(plainDate);
            jobItem.setCardid(e.getCardid());
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

    // 获取卡
    private JCardEntity getCard(String cardid, String cardno) {
        JCardEntity cardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(cardid != null, JCardEntity::getCardid, cardid)
                .eq(cardno != null, JCardEntity::getCardno, cardno)
        );
        if (cardEntity == null) {
            throw new RenException("卡号错误:" + cardno);
        }
        return cardEntity;
    }

}
