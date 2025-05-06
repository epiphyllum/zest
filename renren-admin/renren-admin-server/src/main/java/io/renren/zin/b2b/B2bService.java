package io.renren.zin.b2b;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JB2bDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JB2bEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zcommon.AccessConfig;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.B2bRequester;
import io.renren.zin.BankException;
import io.renren.zin.b2b.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class B2bService {
    @Resource
    private JB2bDao jb2bDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private B2bRequester requester;

    // 文件上传
    private String upload(JMerchantEntity merchant) {
        String uniqueId = CommonUtils.uniqueId();
        String fileId = merchant.getId().toString() + "-" + uniqueId + "-b2b.pdf";
        String filename = zestConfig.getUploadDir() + "/b2b" + "/" + merchant.getId().toString() + ".pdf";
        byte[] bytes;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filename));
        } catch (IOException e) {
            throw new RenException("file not saved correctly");
        }
        requester.upload(merchant.getB2bConfig(), CommonUtils.uniqueId(), bytes, fileId);
        return fileId;
    }

    // 发起生态圈转账:  b2b商户va 到b2b大吉va
    public void ecoTransfer(JMerchantEntity merchant, JB2bEntity jb2bEntity) {
        // 上传协议
        String fid = this.upload(merchant);

        // 设置我方流水ID, 修改状态为发起交易
        String meraplid = CommonUtils.uniqueId();
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getEcoMeraplid, meraplid)
                .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_ECO)
        );

        // 设置请求
        TEcoTransferRequest request = new TEcoTransferRequest();
        request.setAgrefid(fid);
        request.setCurrency(jb2bEntity.getCurrency());
        request.setIslockamount("1");
        request.setMeraplid(meraplid);
        request.setAmount(jb2bEntity.getAmount());
        request.setPayeeaccountno(zestConfig.getB2bConfig().getB2bVa());

        log.info("发起生态圈转账...");
        boolean success = false;
        try {
            TEcoTransferResponse response = requester.request(merchant.getB2bConfig(), CommonUtils.uniqueId(), "/gcpapi/ecosphereTransfer/apply", request, TEcoTransferResponse.class);
            String applyid = response.getApplyid();
            // 更新发起的ecoApplyid
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getEcoApplyid, applyid)
                    .set(JB2bEntity::getError, null)
            );
            success = true;
        } catch (BankException be) {
            // 发起生态圈转账明确失败了
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_NEW)
                    .set(JB2bEntity::getError, be.getMessage())
            );
        }
        if (success) {
            this.ecoVerify(merchant, jb2bEntity);
        }
    }

    // 发起同名转账: b2b大吉va ->  发卡大吉va
    public void fundMerge(JB2bEntity jb2bEntity) {
        // 设置我方流水ID, 修改状态为发起交易
        String meraplid = CommonUtils.uniqueId();
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getFunMeraplid, meraplid)
                .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_MERGE)
        );

        // 请求通联
        TFundMergeRequest request = new TFundMergeRequest();
        request.setMeraplid(meraplid);
        request.setAmount(jb2bEntity.getAmount());
        request.setPayeemerid(zestConfig.getAccessConfig().getMerid());
        request.setCurrency(jb2bEntity.getCurrency());

        AccessConfig b2bConfig = zestConfig.getB2bConfig();
        String requestId = CommonUtils.uniqueId();

        boolean success = false;

        try {
            TFundMergeResponse response = requester.request(b2bConfig, requestId, "/gcpapi/fundmerge/apply", request, TFundMergeResponse.class);
            // 更新funApplyid
            String applyid = response.getApplyid();
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getFunApplyid, applyid)
                    .set(JB2bEntity::getError, null)
            );
            log.info("发起同名转账成功: meraplid:{}, applyid:{}", meraplid, applyid);
            jb2bEntity.setFunApplyid(applyid);
            success = true;
        } catch (BankException be) {
            log.info("发起同名转账失败: meraplid:{}, error:{}", meraplid, be.getMessage());
            // 发起同名转账明确失败了
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getError, be.getMessage())
            );

        }
        if (success) {
            jb2bEntity.setFunMeraplid(meraplid);
            this.fundVerify(jb2bEntity);
        }
    }

    // 申请单查询
    private TCommonQueryResponse commonQuery(AccessConfig b2bConfig, String meraplid) {
        TCommonQuery query = new TCommonQuery();
        query.setMeraplid(meraplid);
        String requestId = CommonUtils.uniqueId();
        TCommonQueryResponse response = requester.request(b2bConfig, requestId, "/gcpapi/apply/query", query, TCommonQueryResponse.class);
        return response;
    }

    // 查询生态圈转账状态
    public void queryEcoTransfer(JMerchantEntity merchant, JB2bEntity jb2bEntity) {
        TCommonQueryResponse response = commonQuery(merchant.getB2bConfig(), jb2bEntity.getEcoMeraplid());

        // 生态圈转账都没有发起, 重新发起生态圈转账
        if (response.getRspcode().equals("1030")) {
            this.ecoTransfer(merchant, jb2bEntity);
            return;
        }
        String state = response.getState();

        if (isFail(state)) {
            this.ecoTransfer(merchant, jb2bEntity);
            return;
        }

        // 生态圈转账是成功的, 直接发起同名转账
        if (state.equals("13")) {
            this.ecoVerify(merchant, jb2bEntity);
            return;
        }

        // 其他情况: 应该更新为已经确认过了
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_ECO_VERIFIED)
        );
    }

    // 查询同名转账
    public void queryFundMerge(JB2bEntity jb2bEntity) {
        TCommonQueryResponse response = commonQuery(zestConfig.getB2bConfig(), jb2bEntity.getFunMeraplid());
        // 同名转账没有发起
        if (response.getRspcode().equals("1030")) {
            log.info("同名转账{}没发起, 发起同名转账...", jb2bEntity.getFunMeraplid());
            this.fundMerge(jb2bEntity);
            return;
        }
        String state = response.getState();

        // 终态
        if (isFail(state)) {
            log.info("同名转账{}失败, 重新发起同名转账...", jb2bEntity.getFunMeraplid());
            this.fundMerge(jb2bEntity);
        }

        // 同名转账为待确认
        if (state.equals("13")) {
            log.info("同名转账待确认, 开始确认...");
            this.fundVerify(jb2bEntity);
            return;
        }

        // 其他情况: 应该更新为已经确认过了
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_MERGE_VERIFIED)
        );
    }

    // 生态圈转账确认
    public void ecoVerify(JMerchantEntity merchant, JB2bEntity jb2bEntity) {
        TB2bConfirmRequest request = new TB2bConfirmRequest();
        request.setApplyid(jb2bEntity.getEcoApplyid());
        request.setExtype("MT");
        log.info("发起生态圈转账确认...");
        try {
            TB2bConfirmResponse response = requester.request(merchant.getB2bConfig(), CommonUtils.uniqueId(), "/gcpapi/b2bwithdraw/confirm", request, TB2bConfirmResponse.class);
            if (!response.getRspcode().equals("0000")) {
                jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                        .eq(JB2bEntity::getId, jb2bEntity.getId())
                        .set(JB2bEntity::getError, response.getRspinfo())
                );
                return;
            }
            // 更新状态
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_ECO_VERIFIED)
                    .set(JB2bEntity::getError, null)
            );
        } catch (BankException be) {
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getError, be.getMessage())
            );
        }
    }

    // 同名转账确认
    public void fundVerify(JB2bEntity jb2bEntity) {
        // 生态圈转账确认
        TB2bConfirmRequest request = new TB2bConfirmRequest();
        request.setApplyid(jb2bEntity.getFunApplyid());
        request.setExtype("MT");
        log.info("发起同名转账确认...");
        try {
            TB2bConfirmResponse response = requester.request(zestConfig.getB2bConfig(), CommonUtils.uniqueId(), "/gcpapi/b2bwithdraw/confirm", request, TB2bConfirmResponse.class);

            if (!response.getRspcode().equals("0000")) {
                jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                        .eq(JB2bEntity::getId, jb2bEntity.getId())
                        .set(JB2bEntity::getError, response.getRspinfo())
                );
                return;
            }

            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_MERGE_VERIFIED)
                    .set(JB2bEntity::getError, null)
            );
        } catch (BankException be) {
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getError, be.getMessage())
            );
        }
    }

    // 同步状态
    public void sync(Long id) {
        JB2bEntity jb2bEntity = jb2bDao.selectById(id);
        if (jb2bEntity == null) {
            throw new RenException("记录不存在");
        }
        JMerchantEntity merchant = jMerchantDao.selectById(jb2bEntity.getMerchantId());
        String state = jb2bEntity.getState();

        // 已经成功了
        if (state.equals(ZinConstant.B2B_MONEY_SUCCESS)) {
            log.info("已经成功");
            return;
        }

        // 同名转账已经确认, 无需关注
        if (state.equals(ZinConstant.B2B_MONEY_MERGE_VERIFIED)) {
            log.info("同名转账已确认");
            return;
        }

        // 已发起同名转账: 查询同名转账状态
        if (state.equals(ZinConstant.B2B_MONEY_MERGE)) {
            log.info("同名转账中, 查询。。。");
            this.queryFundMerge(jb2bEntity);
            return;
        }

        // 商户接入配置
        AccessConfig b2bConfig = null;
        try {
            b2bConfig = objectMapper.readValue(merchant.getB2bva(), AccessConfig.class);
        } catch (JsonProcessingException e) {
            throw new RenException("商户B2B配置有误");
        }
        merchant.setB2bConfig(b2bConfig);

        // 已发起生态圈转账确认: 发起同名转账
        if (state.equals(ZinConstant.B2B_MONEY_ECO_VERIFIED)) {
            this.fundMerge(jb2bEntity);
            return;
        }

        // 已发起生态圈转账:  查询查询
        if (state.equals(ZinConstant.B2B_MONEY_ECO)) {
            this.queryEcoTransfer(merchant, jb2bEntity);
            return;
        }

        // 新建状态: 直接发起生态圈转账
        if (state.equals(ZinConstant.B2B_MONEY_NEW)) {
            this.ecoTransfer(merchant, jb2bEntity);
        }
    }

    // 申请单失败状态
    private boolean isFail(String state) {
        if (state.equals("02") ||
                state.equals("07") ||
                state.equals("23") ||
                state.equals("11")
        ) {
            return true;
        }
        return false;
    }
}
