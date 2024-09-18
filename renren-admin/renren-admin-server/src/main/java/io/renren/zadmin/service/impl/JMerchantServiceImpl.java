package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.mybatis.annotation.DataFilter;
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
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.service.JMerchantService;
import io.renren.zbalance.BalanceType;
import io.renren.zbalance.LedgerConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private SysDeptService sysDeptService;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JBalanceDao jBalanceDao;

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

        // 选父商户
        Long parent = (Long) params.get("parent");
        if (parent != null) {
            wrapper.eq("parent", parent);
        }

        // 选子商户
        Long child = (Long) params.get("child");
        if (child != null) {
            wrapper.ne("parent", 0L);
        }

        //
        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

        return wrapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(JMerchantDTO dto) {
        // 创建通联子商户
        if (dto.getParent() == null) {
            log.info("商户创建...");
            Long master = saveMaster(dto);
            dto.setParent(master);
            saveSub(dto);
            return;
        }
        // 创建大吉商户下的子商户
        log.info("子商户创建...");
        saveSub(dto);
    }


    // 创建大吉商户 | 通联子商户
    public Long saveMaster(JMerchantDTO dto) {
        UserDetail user = SecurityUser.getUser();

        Long agentId = dto.getAgentId();
        SysDeptEntity agentDept = sysDeptDao.selectById(agentId);
        String agentName = agentDept.getName();
        String pids = user.getDeptId() + "," + agentId;   // todo: check

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

        // 15 * 5个账户
        for (String currency : BalanceType.CURRENCY_LIST) {
            newBalance(deptEntity, BalanceType.getInAccount(currency), "HKD");
            newBalance(deptEntity, BalanceType.getDepositAccount(currency), currency);  //  预收保证金
            newBalance(deptEntity, BalanceType.getChargeFeeAccount(currency), currency); // 充值到卡手续费
            newBalance(deptEntity, BalanceType.getTxnFeeAccount(currency), currency);  // 预收交易手续费
            newBalance(deptEntity, BalanceType.getVaAccount(currency), currency);  // 创建va账户
        }

        insert(jMerchantEntity);
        return deptEntity.getId();
    }

    public void saveSub(JMerchantDTO dto) {
        UserDetail user = SecurityUser.getUser();

        // 自商户部门id是三级别:  大吉Id, agentId, merchantId
        Long djId = user.getDeptId();
        Long agentId = dto.getAgentId();
        Long merchantId = dto.getParent();

        String pids = djId + "," + agentId + "," + merchantId;

        // 商户部门
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setPids(pids);
        deptEntity.setName(dto.getCusname());
        deptEntity.setPid(dto.getParent());  // 子商户的parent是商户
        deptEntity.setParentName(sysDeptDao.selectById(dto.getParent()).getName());
        sysDeptService.insert(deptEntity);

        // 父商户
        SysDeptEntity parentDept = sysDeptDao.selectById(dto.getParent());

        // 属性copy
        JMerchantEntity jMerchantEntity = ConvertUtils.sourceToTarget(dto, JMerchantEntity.class);

        // 子商户->父商户信息
        jMerchantEntity.setParent(dto.getParent());
        jMerchantEntity.setParentName(parentDept.getName());
        jMerchantEntity.setAgentId(agentId);
        jMerchantEntity.setAgentName(dto.getAgentName());
        jMerchantEntity.setId(deptEntity.getId());

        // 创建管理账户 - 按币种来: 15 * 3
        for (String currency : BalanceType.CURRENCY_LIST) {
            newBalance(deptEntity, BalanceType.getSubSumAccount(currency), currency);
            newBalance(deptEntity, BalanceType.getSubFeeAccount(currency), currency);
            newBalance(deptEntity, BalanceType.getSubVaAccount(currency), currency);
        }

        // 创建子商户
        insert(jMerchantEntity);
    }


    /**
     *
     */
    private void newBalance(SysDeptEntity deptEntity, String type, String currency) {
        JBalanceEntity jBalanceEntity = new JBalanceEntity();

        jBalanceEntity.setOwnerId(deptEntity.getId());
        jBalanceEntity.setOwnerName(deptEntity.getName());
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