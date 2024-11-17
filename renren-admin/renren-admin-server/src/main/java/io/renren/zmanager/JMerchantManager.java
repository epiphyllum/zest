package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.service.JMerchantService;
import io.renren.zbalance.BalanceType;
import io.renren.zcommon.ZestConstant;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JMerchantManager {

    @Resource
    private JMerchantService jMerchantService;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JSubDao jSubDao;
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
    @Resource
    private JSubManager jSubManager;

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
        // this.uploadFiles(jMerchantEntity);

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

//        // 15 * 5 = 75个账户
//        for (String currency : BalanceType.CURRENCY_LIST) {
//            newBalance(entity, BalanceType.getVaAccount(currency), currency);  // 创建va账户
//        }
        log.info("openva, currencyList: {}", entity.getCurrencyList());
        String[] currencyList = entity.getCurrencyList().split(",");
        for (String currency : currencyList) {
            newBalance(entity, BalanceType.getVaAccount(currency), currency);  // 创建va账户
        }

    }

    /**
     * 创建账户
     */
    private void newBalance(JMerchantEntity entity, String type, String currency) {
        log.info("newBalance: {}, {}, {}", entity, type, currency);
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
        // this.uploadFiles(jMerchantEntity);

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
        String credifid = jMerchantEntity.getCredifid();
        String legalphotobackfid = jMerchantEntity.getLegalphotobackfid();
        String legalphotofrontfid = jMerchantEntity.getLegalphotofrontfid();
        String organfid = jMerchantEntity.getOrganfid();
        List<String> fids = new ArrayList<>();
        if (agreementfid != null) {
            fids.add(agreementfid);
        }
        if (buslicensefid != null) {
            fids.add(buslicensefid);
        }
        if (credifid != null) {
            fids.add(credifid);
        }
        if (legalphotobackfid != null) {
            fids.add(legalphotobackfid);
        }
        if (legalphotofrontfid != null) {
            fids.add(legalphotofrontfid);
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
                // 新增加默认子商户
                JSubEntity jSubEntity = ConvertUtils.sourceToTarget(entity, JSubEntity.class);
                jSubEntity.setCusname(jSubEntity.getCusname() + "-默认");
                jSubEntity.setState(ZinConstant.MERCHANT_STATE_REGISTER);
                jSubEntity.setMerchantId(entity.getId());
                jSubEntity.setApi(0);
                jSubManager.save(jSubEntity);
            }
        });
    }


    public void update(JMerchantDTO dto) {
        JMerchantEntity merchant = jMerchantDao.selectById(dto.getId());

        // 还没有通过
        if (!merchant.getState().equals(ZinConstant.MERCHANT_STATE_REGISTER)) {
            jMerchantService.update(dto);
            return;
        }

        // 原始币种列表
        String[] oldList = merchant.getCurrencyList().split(",");
        Set<String> oldSet = new HashSet<>();
        oldSet.addAll(List.of(oldList));

        // 新币种列表
        String[] newList = merchant.getCurrencyList().split(",");
        Set<String> newSet = new HashSet<>();
        newSet.addAll(List.of(newList));

        // 计算减少和增加的币种
        Set<String> added = new HashSet<String>(newSet);
        Set<String> removed = new HashSet<String>(oldSet);
        added.removeAll(oldSet);
        removed.removeAll(newSet);

        // 如果有减少币种, 报错
        if (removed.size() > 0) {
            throw new RenException("不能删除支持的币种");
        }

        // 没有增加币种
        if (newSet.size() == 0) {
            jMerchantService.update(dto);
            return;
        }

        // 子商户列表(审核通过的)
        List<JSubEntity> subEntities = jSubDao.selectList(Wrappers.<JSubEntity>lambdaQuery()
                .eq(JSubEntity::getMerchantId, dto.getId())
                .eq(JSubEntity::getState, ZinConstant.MERCHANT_STATE_VERIFIED)
        );
        tx.executeWithoutResult(status -> {
            // 新增加的币种
            for (String currency : added) {
                newBalance(merchant, BalanceType.getVaAccount(currency), currency);  // 创建va账户
            }
            // 子商户增加币种
            for (JSubEntity subEntity : subEntities) {
                jSubManager.openSubVa(subEntity, added);
            }
        });
    }
}
