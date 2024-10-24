package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zbalance.BalanceType;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.file.ZinFileService;
import io.renren.zin.sub.ZinSubService;
import io.renren.zin.sub.dto.TSubCreateRequest;
import io.renren.zin.sub.dto.TSubCreateResponse;
import io.renren.zin.sub.dto.TSubQuery;
import io.renren.zin.sub.dto.TSubQueryResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JMerchantManager {

    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ZinSubService zinSubService;
    @Resource
    private SysDeptDao sysDeptDao;

    public JMerchantEntity save(JMerchantDTO dto) {

        JMerchantEntity jMerchantEntity = ConvertUtils.sourceToTarget(dto, JMerchantEntity.class);
        // 拿到用户
        UserDetail user = SecurityUser.getUser();
        // 商户所属代理
        Long agentId = dto.getAgentId();
        if (user.getUserType().equals(ZestConstant.USER_TYPE_OPERATION)) {
            if (agentId == null) {
                throw new RenException("agentId is not provided");
            }
        }
        if (user.getUserType().equals(ZestConstant.USER_TYPE_AGENT)) {
            agentId = user.getDeptId();
        }

        // 上传商户附件
        this.uploadFiles(jMerchantEntity);

        SysDeptEntity agentDept = sysDeptDao.selectById(agentId);
        String agentName = agentDept.getName();
        String pids = agentDept.getPid() + "," + agentId;

        // 创建商户部门
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setPids(pids);
        deptEntity.setName(dto.getCusname());
        deptEntity.setPid(agentId);  // 属于agentId
        deptEntity.setParentName(agentName);

        jMerchantEntity.setAgentId(agentId);
        jMerchantEntity.setAgentName(agentName);

        // daji initial state!!!
        jMerchantEntity.setState(ZinConstant.MERCHANT_STATE_TO_VERIFY);

        return tx.execute(st -> {
            sysDeptDao.insert(deptEntity);
            // 商户部门参数
            jMerchantEntity.setId(deptEntity.getId());  // 商户ID
            jMerchantDao.insert(jMerchantEntity);
            return jMerchantEntity;
        });

    }

    /**
     * 商户开通VA以及管理账户
     */
    public void openVa(JMerchantEntity entity) {
        // 15 * 5 = 75个账户
        tx.executeWithoutResult(st -> {
            for (String currency : BalanceType.CURRENCY_LIST) {
                newBalance(entity, BalanceType.getDepositAccount(currency), currency);  //  预收保证金
                newBalance(entity, BalanceType.getChargeFeeAccount(currency), currency); // 充值到卡手续费
                newBalance(entity, BalanceType.getTxnFeeAccount(currency), currency);  // 预收交易手续费
                newBalance(entity, BalanceType.getVaAccount(currency), currency);  // 创建va账户
            }
        });
    }

    /**
     * 创建账户
     */
    private void newBalance(JMerchantEntity entity, String type, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(entity.getId());
        jBalanceEntity.setOwnerName(entity.getCusname());
        jBalanceEntity.setOwnerType(ZestConstant.USER_TYPE_MERCHANT);
        jBalanceEntity.setBalanceType(type);
        jBalanceEntity.setCurrency(currency);
        jBalanceDao.insert(jBalanceEntity);
    }


    /**
     * 提交通联
     *
     * @param jMerchantEntity
     */
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

    /**
     * 上传通联文件:  deprecated
     *
     * @param jMerchantEntity
     */
    private void uploadFiles(JMerchantEntity jMerchantEntity) {
        // 拿到所有文件fid
        String agreementfid = jMerchantEntity.getAgreementfid();
        String buslicensefid = jMerchantEntity.getBuslicensefid();
        String creditfid = jMerchantEntity.getCreditfid();
        String legalphotobackfid = jMerchantEntity.getLegalphotobackfid();
        String legalphotofrontfid = jMerchantEntity.getLegalphotofrontfid();
        String taxfid = jMerchantEntity.getTaxfid();
        String organfid = jMerchantEntity.getOrganfid();

        List<String> fids = new ArrayList<>();
        if (agreementfid != null) {
            fids.add(agreementfid);
        }
        if (buslicensefid != null) {
            fids.add(buslicensefid);
        }
        if (creditfid != null) {
            fids.add(agreementfid);
        }
        if (legalphotobackfid != null) {
            fids.add(legalphotobackfid);
        }
        if (legalphotofrontfid != null) {
            fids.add(legalphotofrontfid);
        }
        if (taxfid != null) {
            fids.add(taxfid);
        }
        if (organfid != null) {
            fids.add(organfid);
        }

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

    /**
     * 查询通联
     *
     * @param jMerchantEntity
     */
    public void query(JMerchantEntity jMerchantEntity) {
        TSubQuery tSubQuery = ConvertUtils.sourceToTarget(jMerchantEntity, TSubQuery.class);
        TSubQueryResponse response = zinSubService.query(tSubQuery);
        this.changeState(jMerchantEntity, response.getState(), response.getCusid());
    }

    public void changeState(JMerchantEntity entity, String newState, String cusid) {
        String oldState = entity.getState();
        log.info("{}: state {} -> {}", entity.getCusname(), oldState, newState);

        // 状态没有变化
        if (oldState.equals(newState)) {
            return;
        }

        // 变为成功, 并开户
        tx.executeWithoutResult(st -> {
            jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                    .eq(JMerchantEntity::getId, entity.getId())
                    .eq(JMerchantEntity::getState, oldState)
                    .set(JMerchantEntity::getState, newState)
                    .set(cusid != null, JMerchantEntity::getCusid, cusid)
            );
            // 状态又变化， 且新状态是成功
            if (newState.equals(ZinConstant.MERCHANT_STATE_VERIFIED) || newState.equals(ZinConstant.MERCHANT_STATE_REGISTER)) {
                this.openVa(entity);
            }
        });
    }
}
