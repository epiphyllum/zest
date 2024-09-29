package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.service.SysDeptService;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JSubDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.service.JSubService;
import io.renren.zbalance.BalanceType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * j_merchant
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Service
@Slf4j
public class JSubServiceImpl extends CrudServiceImpl<JSubDao, JSubEntity, JSubDTO> implements JSubService {
    @Resource
    private SysDeptService sysDeptService;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JBalanceDao jBalanceDao;

    @Override
    public PageData<JSubDTO> page(Map<String, Object> params) {
        IPage<JSubEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JSubDTO.class);
    }

    @Override
    public QueryWrapper<JSubEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JSubEntity> wrapper = new QueryWrapper<>();

        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

        CommonFilter.setFilterMerchant(wrapper, params);

//        String agentId = (String) params.get("agentId");
//        if (StringUtils.isNotBlank(agentId)) {
//            wrapper.eq("agent_id", Long.parseLong(agentId));
//        }
//        String merchantId = (String) params.get("merchantId");
//        if (StringUtils.isNotBlank(merchantId)) {
//            wrapper.eq("merchant_id", Long.parseLong(merchantId));
//        }
//        UserDetail user = SecurityUser.getUser();
//        if (agentId != null && ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
//            // 代理访问
//            wrapper.eq("agent_id", user.getDeptId());
//        } else if (merchantId == null && ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
//            // 商户访问
//            wrapper.eq("merchant_id", user.getDeptId());
//        }

        return wrapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(JSubDTO dto) {
        saveSub(dto);
    }

    public void saveSub(JSubDTO dto) {
        UserDetail user = SecurityUser.getUser();

        if (user.getUserType() == null) {
            throw new RenException("invalid request user");
        }

        Long djId = null;

        if (user.getUserType().equals(ZestConstant.USER_TYPE_OPERATION)) {
            // 机构操作
            djId = user.getDeptId();
            if (dto.getAgentId() == null || dto.getMerchantId() == null) {
                throw new RenException("invalid request");
            }
        } else if (user.getUserType().equals(ZestConstant.USER_TYPE_AGENT)) {
            // 代理操作
            if (dto.getMerchantId() == null) {
                throw new RenException("invalid request");
            }
            SysDeptEntity agentDept = sysDeptDao.getById(user.getDeptId());
            djId = agentDept.getPid();
            // 设置agent
            dto.setAgentId(agentDept.getId());
            dto.setAgentName(agentDept.getName());
        } else if (user.getUserType().equals(ZestConstant.USER_TYPE_MERCHANT)) {
            // 商户操作
            SysDeptEntity merchantDept = sysDeptDao.getById(user.getDeptId());
            String[] split = merchantDept.getPids().split(",");
            djId = Long.parseLong(split[0]);
            Long agentId = Long.parseLong(split[1]);
            SysDeptEntity agentDept = sysDeptDao.selectById(agentId);

            // 设置agent
            dto.setAgentId(agentId);
            dto.setAgentName(agentDept.getName());

            // 设置merchant
            dto.setMerchantName(user.getDeptName());
            dto.setMerchantId(user.getDeptId());
        } else {
            throw new RenException("internal error");
        }

        // 自商户部门id是三级别:  大吉Id, agentId, merchantId
        String pids = djId + "," + dto.getAgentId() + "," + dto.getMerchantId();

        // 子商户部门
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setPids(pids);
        deptEntity.setName(dto.getCusname());
        sysDeptService.insert(deptEntity);

        // 属性copy
        JSubEntity jSubEntity = ConvertUtils.sourceToTarget(dto, JSubEntity.class);

        // 子商户的ID 就是子商户的部门ID
        jSubEntity.setId(deptEntity.getId());

        // 创建管理账户 - 按币种来: 15 * 3
        for (String currency : BalanceType.CURRENCY_LIST) {
            newBalance(deptEntity, BalanceType.getSubSumAccount(currency), currency);
            newBalance(deptEntity, BalanceType.getSubFeeAccount(currency), currency);
            newBalance(deptEntity, BalanceType.getSubVaAccount(currency), currency);
        }

        // 创建子商户
        insert(jSubEntity);
    }


    /**
     *
     */
    private void newBalance(SysDeptEntity deptEntity, String type, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(deptEntity.getId());
        jBalanceEntity.setOwnerName(deptEntity.getName());
        jBalanceEntity.setOwnerType("sub");
        jBalanceEntity.setBalanceType(type);
        jBalanceEntity.setCurrency(currency);
        jBalanceDao.insert(jBalanceEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        // 先删除账户
        jBalanceDao.delete(Wrappers.<JBalanceEntity>lambdaQuery()
                .in(JBalanceEntity::getOwnerId, idList)
        );
        // 删除自己数据
        baseDao.deleteByIds(idList);
        // 删除部门信息
        sysDeptDao.deleteByIds(idList);
    }

}