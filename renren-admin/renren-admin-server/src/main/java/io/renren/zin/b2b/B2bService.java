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

    private String upload(JMerchantEntity merchant) {
        String uniqueId = CommonUtils.uniqueId();
        String fileId = merchant.getId().toString() + "-" + uniqueId + "-b2b.pdf";
        String filename = zestConfig.getUploadDir() + "/b2b" +  "/" +  merchant.getId().toString() + ".pdf";
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

        // 设置流水ID
        String meraplid = CommonUtils.uniqueId();
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getEcoMeraplid, meraplid)
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
        TEcoTransferResponse response = requester.request(zestConfig.getB2bConfig(), CommonUtils.uniqueId(), "/gcpapi/ecosphereTransfer", request, TEcoTransferResponse.class);

        String applyid = response.getApplyid();
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getEcoApplyid, applyid)
        );

    }

    // 发起同名转账: b2b大吉va ->  发卡大吉va
    public void fundMerge(JB2bEntity jb2bEntity) {
        // 请求通联
        TFundMergeRequest request = new TFundMergeRequest();
        request.setMeraplid(jb2bEntity.getFunMeraplid());
        request.setAmount(jb2bEntity.getAmount());
        request.setPayeemerid(zestConfig.getAccessConfig().getMerid());
        request.setCurrency(jb2bEntity.getCurrency());

        AccessConfig b2bConfig = zestConfig.getB2bConfig();
        String requestId = CommonUtils.uniqueId();
        TFundMergeResponse response = requester.request(b2bConfig, requestId, "/gcpapi/fundmerge/apply", request, TFundMergeResponse.class);

        // 更新
        String applyid = response.getApplyid();
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getFunApplyid, applyid)
        );
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
        // 说明生态圈转账都没有发起
        if (jb2bEntity.getEcoApplyid() == null && response.getRspcode().equals("1030") ) {
            // 重新发起
            this.ecoTransfer(merchant, jb2bEntity);
            return;
        }
        String state = response.getState();

        // 成功 -> 触发同名转账
        if (state.equals("06")) {
            this.fundMerge(jb2bEntity);
            return;
        }

        // 失败 -> 重新发起
        if ( state.equals("02") ||
                        state.equals("07") ||
                        state.equals("23") ||
                        state.equals("11")
        ) {
            String ecoMeraplid = CommonUtils.uniqueId();
            jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                    .eq(JB2bEntity::getId, jb2bEntity.getId())
                    .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_NEW)
                    .set(JB2bEntity::getEcoApplyid, null)
                    .set(JB2bEntity::getEcoMeraplid, ecoMeraplid)
            );

            // 重新发起生态圈转账
            this.ecoTransfer(merchant, jb2bEntity);
        }

        // 处理中: 不管
    }

    // 查询同名转账
    public void queryFundMerge(JB2bEntity jb2bEntity) {
        TCommonQueryResponse tCommonQueryResponse = commonQuery(zestConfig.getB2bConfig(), jb2bEntity.getEcoMeraplid());
    }

    // 同步状态
    public void sync(Long id) {
        JB2bEntity jb2bEntity = jb2bDao.selectById(id);
        if (jb2bEntity == null) {
            throw new RenException("记录不存在");
        }

        // 新建状态
        if (jb2bEntity.getState().equals(ZinConstant.B2B_MONEY_NEW)) {
            JMerchantEntity merchant = jMerchantDao.selectById(jb2bEntity.getMerchantId());
            this.ecoTransfer(merchant,jb2bEntity);
            return;
        }

        // 已发起生态圈转账: 需要查询生态圈转账
        if (jb2bEntity.getState().equals(ZinConstant.B2B_MONEY_ECO)) {
            JMerchantEntity merchant = jMerchantDao.selectById(jb2bEntity.getMerchantId());
            AccessConfig b2bConfig = null;
            try {
                b2bConfig = objectMapper.readValue(merchant.getB2bva(), AccessConfig.class);
            } catch (JsonProcessingException e) {
                throw new RenException("商户B2B配置有误");
            }
            merchant.setB2bConfig(b2bConfig);
            this.queryEcoTransfer(merchant, jb2bEntity);
            return;
        }

        // 已发起同名转账: 需要查询同名转账
        if (jb2bEntity.getState().equals(ZinConstant.B2B_MONEY_MERGE)) {
            this.queryFundMerge(jb2bEntity);
            return;
        }
    }

}
