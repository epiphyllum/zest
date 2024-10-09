package io.renren.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.service.SysDeptService;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JSubDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zbalance.BalanceType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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

    /**
     * 审核通过
     *
     * @param id
     * @param state
     */
    public void verify(Long id, String state) {
        JSubEntity entity = jSubDao.selectById(id);
        if (!"06".equals(entity.getState()) && "06".equals(state)) {
            tx.executeWithoutResult(st -> {
                this.openSubVa(entity);
                jSubDao.update(null, Wrappers.<JSubEntity>lambdaUpdate()
                        .eq(JSubEntity::getId, id)
                        .set(JSubEntity::getState, "06")
                );
            });
            return;
        }
        jSubDao.update(null, Wrappers.<JSubEntity>lambdaUpdate()
                .eq(JSubEntity::getId, id)
                .set(JSubEntity::getState, state)
        );
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

    public void save(JSubDTO dto) {
        // 属性copy
        JSubEntity jSubEntity = ConvertUtils.sourceToTarget(dto, JSubEntity.class);
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
    public void openSubVa(JSubEntity entity) {
        // 创建管理账户 - 按币种来: 15 * 3
        tx.executeWithoutResult(st -> {
            for (String currency : BalanceType.CURRENCY_LIST) {
                newBalance(entity, BalanceType.getSubSumAccount(currency), currency);
                newBalance(entity, BalanceType.getSubFeeAccount(currency), currency);
                newBalance(entity, BalanceType.getSubVaAccount(currency), currency);
            }
        });
    }

    /**
     * 创建账户
     */
    private void newBalance(JSubEntity entity, String type, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(entity.getId());
        jBalanceEntity.setOwnerName(entity.getCusname());
        jBalanceEntity.setOwnerType("sub");  // attention
        jBalanceEntity.setBalanceType(type);
        jBalanceEntity.setCurrency(currency);
        jBalanceDao.insert(jBalanceEntity);
    }
}
