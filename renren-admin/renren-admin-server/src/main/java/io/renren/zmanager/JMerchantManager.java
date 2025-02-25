package io.renren.zmanager;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.service.JMerchantService;
import io.renren.zbalance.BalanceType;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZestConstant;
import io.renren.zcommon.ZinConstant;
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

@Service
@Slf4j
public class JMerchantManager {

    @Resource
    private JMerchantService jMerchantService;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinSubService zinSubService;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JSubManager jSubManager;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private ZestConfig zestConfig;

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
        String[] currencyList = entity.getCurrencyList().split(",");
        for (String currency : currencyList) {
            ledgerUtil.newBalance(ZestConstant.USER_TYPE_MERCHANT, entity.getCusname(), entity.getId(), BalanceType.getVaAccount(currency), currency);
        }
    }

    /**
     * 提交通联
     *
     * @param jMerchantEntity
     */
    public void submit(JMerchantEntity jMerchantEntity) {
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
            int update = jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                    .eq(JMerchantEntity::getId, entity.getId())
                    .eq(JMerchantEntity::getState, oldState)
                    .set(JMerchantEntity::getState, newState)
                    .set(cusid != null, JMerchantEntity::getCusid, cusid)
            );
            if (update != 1) {
                throw new RenException("开户失败");
            }
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
                // 默认子商户自动审核通过
                jSubManager.verify(jSubEntity.getId(), ZinConstant.MERCHANT_STATE_VERIFIED);
            }
        });
    }


    public void update(JMerchantDTO dto) {
        JMerchantEntity merchant = jMerchantDao.selectById(dto.getId());

        // 还没有通过
        if (!merchant.getState().equals(ZinConstant.MERCHANT_STATE_REGISTER)) {
            log.info("商户尚未注册, 可以做修改");
            jMerchantService.update(dto);
            return;
        }

        log.info("商户已经注册，查看币种修改情况...");

        // 原始币种列表
        String[] oldList = merchant.getCurrencyList().split(",");
        Set<String> oldSet = new HashSet<>();
        oldSet.addAll(List.of(oldList));

        // 新币种列表
        String[] newList = dto.getCurrencyList().split(",");
        Set<String> newSet = new HashSet<>();
        newSet.addAll(List.of(newList));

        // 计算减少和增加的币种
        Set<String> added = new HashSet<String>(newSet);
        Set<String> removed = new HashSet<String>(oldSet);
        added.removeAll(oldSet);
        removed.removeAll(newSet);

        log.info("增加币种:{}, 减少币种:{}", added.size(), removed.size());

        // 如果有减少币种, 报错
        if (removed.size() > 0) {
            throw new RenException("不能删除支持的币种");
        }

        // 没有增加币种
        if (added.size() == 0) {
            jMerchantService.update(dto);
            return;
        }

        // 子商户列表(审核通过的)
        List<JSubEntity> subEntities = jSubDao.selectList(Wrappers.<JSubEntity>lambdaQuery()
                .eq(JSubEntity::getMerchantId, dto.getId())
                .eq(JSubEntity::getState, ZinConstant.MERCHANT_STATE_VERIFIED)
        );
        tx.executeWithoutResult(status -> {
            // 更新商户信息
            jMerchantService.update(dto);
            // 新增加的币种
            for (String currency : added) {
                ledgerUtil.newBalance(ZestConstant.USER_TYPE_MERCHANT, merchant.getCusname(), merchant.getId(), BalanceType.getVaAccount(currency), currency);
            }
            // 子商户增加币种
            for (JSubEntity subEntity : subEntities) {
                jSubManager.openSubVa(subEntity, added);
            }
        });
    }

    public Map<String, String> getConfig(Long merchantId) {
        String platformKey = zestConfig.getPublicKey();
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
        Map<String, String> config = new HashMap<>();
        config.put("platformKey", platformKey);
        config.put("webhook", merchant.getWebhook());
        config.put("publicKey", merchant.getPublicKey());
        config.put("sensitiveKey", merchant.getSensitiveKey());
        config.put("whiteIp", merchant.getWhiteIp());
        return config;
    }

    public void setConfig(String key, String value, int otp) {
        UserDetail user = SecurityUser.getUser();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean authorized = gAuth.authorize(user.getTotpKey(), otp);
        if (!authorized) {
            throw new RenException("谷歌验证码错误");
        }
        LambdaUpdateWrapper<JMerchantEntity> wrapper = Wrappers.<JMerchantEntity>lambdaUpdate().eq(JMerchantEntity::getId, user.getId());
        if (key.equals("webhook")) {
            wrapper.set(JMerchantEntity::getWebhook, value);
        } else if (key.equals("whiteIp")) {
            wrapper.set(JMerchantEntity::getWhiteIp, value);
        } else if (key.equals("sensitiveKey")) {
            wrapper.set(JMerchantEntity::getSensitiveKey, value);
        } else if (key.equals("publicKey")) {
            wrapper.set(JMerchantEntity::getPublicKey, value);
        }
        jMerchantDao.update(null, wrapper);
    }

}
