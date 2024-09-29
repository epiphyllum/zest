package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.service.SysDeptService;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.dto.JSubDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.service.JMerchantService;
import io.renren.zadmin.service.JSubService;
import io.renren.zadmin.zorg.member.JMerchantController;
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
public class JMerchantServiceImpl extends CrudServiceImpl<JMerchantDao, JMerchantEntity, JMerchantDTO> implements JMerchantService {
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JSubService jSubService;

    @Override
    public PageData<JMerchantDTO> page(Map<String, Object> params) {
        IPage<JMerchantEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JMerchantDTO.class);
    }

    @Override
    public QueryWrapper<JMerchantEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMerchantEntity> wrapper = new QueryWrapper<>();

        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

//        String agentId = (String) params.get("agentId");
//        if (StringUtils.isNotBlank(agentId)) {
//            wrapper.eq("agent_id", Long.parseLong(agentId));
//        }
//        UserDetail user = SecurityUser.getUser();
//        if (agentId != null && ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
//            wrapper.eq("agent_id", user.getDeptId());
//        }

        CommonFilter.setFilterAgent(wrapper, params);

        return wrapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(JMerchantDTO dto) {
        JMerchantEntity merchant = saveMaster(dto);

        JSubDTO jSubDTO = ConvertUtils.sourceToTarget(merchant, JSubDTO.class);
        jSubDTO.setMerchantId(merchant.getId());
        jSubDTO.setMerchantName(merchant.getCusname());

        jSubService.save(jSubDTO);
    }


    // 创建大吉商户
    public JMerchantEntity saveMaster(JMerchantDTO dto) {

        // 拿到用户
        UserDetail user = SecurityUser.getUser();

        // 商户所属代理
        Long agentId = dto.getAgentId();
        SysDeptEntity agentDept = sysDeptDao.selectById(agentId);
        String agentName = agentDept.getName();
        String pids = user.getDeptId() + "," + agentId;

        // 创建商户部门
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setPids(pids);
        deptEntity.setName(dto.getCusname());
        deptEntity.setPid(agentId);  // 属于agentId
        deptEntity.setParentName(agentName);
        sysDeptDao.insert(deptEntity);

        // 商户部门参数
        JMerchantEntity jMerchantEntity = ConvertUtils.sourceToTarget(dto, JMerchantEntity.class);
        jMerchantEntity.setAgentId(agentId);
        jMerchantEntity.setAgentName(agentName);
        jMerchantEntity.setId(deptEntity.getId());  // 商户ID

        // 15 * 5 = 75个账户
        for (String currency : BalanceType.CURRENCY_LIST) {
            newBalance(deptEntity, BalanceType.getInAccount(currency), "HKD");
            newBalance(deptEntity, BalanceType.getDepositAccount(currency), currency);  //  预收保证金
            newBalance(deptEntity, BalanceType.getChargeFeeAccount(currency), currency); // 充值到卡手续费
            newBalance(deptEntity, BalanceType.getTxnFeeAccount(currency), currency);  // 预收交易手续费
            newBalance(deptEntity, BalanceType.getVaAccount(currency), currency);  // 创建va账户
        }

        insert(jMerchantEntity);

        return jMerchantEntity;
    }

    /**
     *
     */
    private void newBalance(SysDeptEntity deptEntity, String type, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();
        jBalanceEntity.setOwnerId(deptEntity.getId());
        jBalanceEntity.setOwnerName(deptEntity.getName());
        jBalanceEntity.setOwnerType("merchant");
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