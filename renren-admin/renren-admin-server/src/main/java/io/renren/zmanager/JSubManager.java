package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.service.SysDeptService;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.sub.ApiSubService;
import io.renren.zapi.sub.dto.SubNotify;
import io.renren.zcommon.ZapiConstant;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zbalance.BalanceType;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class JSubManager {

    @Resource
    private TransactionTemplate tx;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private SysDeptService sysDeptService;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private ApiNotifyService apiNotifyService;

    /**
     * 审核通过
     *
     * @param id
     * @param state
     */
    public void verify(Long id, String state) {
        JSubEntity entity = jSubDao.selectById(id);

        String oldState = entity.getState();
        if (oldState.equals(ZinConstant.MERCHANT_STATE_VERIFIED) || oldState.equals(ZinConstant.MERCHANT_STATE_FAIL)) {
            throw new RenException("当前状态不正确:" + state);
        }

        // 查询商户
        Long merchantId = entity.getMerchantId();
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);

        // 审核通过
        if (!ZinConstant.MERCHANT_STATE_VERIFIED.equals(entity.getState()) && ZinConstant.MERCHANT_STATE_VERIFIED.equals(state)) {
            List<String> currencyList = Arrays.stream(merchant.getCurrencyList().split(",")).toList();

            tx.executeWithoutResult(st -> {
                // 开通子商户VA
                this.openSubVa(entity, currencyList);
                jSubDao.update(null, Wrappers.<JSubEntity>lambdaUpdate()
                        .eq(JSubEntity::getId, id)
                        .set(JSubEntity::getState, ZinConstant.MERCHANT_STATE_VERIFIED)
                );
            });
        }

        // 审核不通过
        jSubDao.update(null, Wrappers.<JSubEntity>lambdaUpdate()
                .eq(JSubEntity::getId, id)
                .set(JSubEntity::getState, state)
        );

        // 接口创建
        if (entity.getApi().equals(1)) {
            SubNotify subNotify = new SubNotify(id, entity.getCusname(), state);
            apiNotifyService.notifyMerchant(subNotify, merchant, ZapiConstant.API_subNotify);
        }
    }

    public Long fill(JSubEntity entity) {
        UserDetail user = SecurityUser.getUser();
        if (user.getUserType() == null) {
            throw new RenException("invalid request user");
        }

        Long djId = null;
        if (ZestConstant.USER_TYPE_OPERATION.equals(user.getUserType()) ||
                ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())
        ) {
            if (entity.getMerchantId() == null) {
                throw new RenException("invalid request: need merchantId");
            }
        } else if (user.getUserType().equals(ZestConstant.USER_TYPE_MERCHANT)) {
            entity.setMerchantId(user.getDeptId());
        }

        // 查询商户, 填充 agent, merchant
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setMerchantName(merchant.getCusname());

        // dj ID查找
        if (ZestConstant.USER_TYPE_OPERATION.equals(user.getUserType())) {
            djId = user.getDeptId();
        } else if (ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
            SysDeptEntity agentDept = sysDeptDao.selectById(user.getDeptId());
            djId = agentDept.getPid();
        } else if (ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
            SysDeptEntity merchantDept = sysDeptDao.getById(merchant.getId());
            String[] split = merchantDept.getPids().split(",");
            djId = Long.parseLong(split[0]);
        } else {
            throw new RenException("invalid user type");
        }

        return djId;
    }

    public void save(JSubEntity dto) {
        // 属性copy
        JSubEntity jSubEntity = ConvertUtils.sourceToTarget(dto, JSubEntity.class);
        jSubEntity.setState(ZinConstant.MERCHANT_STATE_TO_VERIFY);

        // 填充
        Long djId = this.fill(jSubEntity);
        // 自商户部门id是三级别:  大吉Id, agentId, merchantId
        String pids = djId + "," + jSubEntity.getAgentId() + "," + jSubEntity.getMerchantId();
        // 子商户部门
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setPids(pids);
        deptEntity.setName(dto.getCusname());
        deptEntity.setPid(dto.getMerchantId());

        tx.executeWithoutResult(st -> {
            sysDeptService.insert(deptEntity);
            // 子商户的ID 就是子商户的部门ID
            jSubEntity.setId(deptEntity.getId());
            // 创建子商户
            jSubDao.insert(jSubEntity);
        });
    }

    /**
     * 创建子商户va
     *
     * @param entity
     */
    public void openSubVa(JSubEntity entity, Collection<String> currencyList) {
        // 创建管理账户 - 按币种来: 15 * 6
        for (String currency : currencyList) {
            newBalance(entity, BalanceType.getSubVaAccount(currency), currency);      // 子商户va
            // 子商户
            newBalance(entity, BalanceType.getDepositAccount(currency), currency);    // 子商户保证
            newBalance(entity, BalanceType.getChargeAccount(currency), currency);     // 子商户充值手续费
            newBalance(entity, BalanceType.getCardFeeAccount(currency), currency);    // 子商户开卡费用
            newBalance(entity, BalanceType.getCardSumAccount(currency), currency);    // 子商户卡充值总额
            newBalance(entity, BalanceType.getTxnAccount(currency), currency);        // 子商户其他费用
            // 成本账户
            newBalance(entity, BalanceType.getAipDepositAccount(currency), currency);  // 子商户保证
            newBalance(entity, BalanceType.getAipChargeAccount(currency), currency);   // 子商户充值手续费
            newBalance(entity, BalanceType.getAipCardFeeAccount(currency), currency);  // 子商户开卡费用
            newBalance(entity, BalanceType.getAipCardSumAccount(currency), currency);  // 子商户卡充值总额
            newBalance(entity, BalanceType.getAipTxnAccount(currency), currency);      // 子商户其他费用
        }
    }

    /**
     * 创建账户
     */
    private void newBalance(JSubEntity entity, String type, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(entity.getId());
        jBalanceEntity.setOwnerName(entity.getCusname());
        jBalanceEntity.setOwnerType(ZestConstant.USER_TYPE_SUB);  // attention
        jBalanceEntity.setBalanceType(type);
        jBalanceEntity.setCurrency(currency);
        jBalanceDao.insert(jBalanceEntity);
    }
}
