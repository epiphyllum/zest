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
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.entity.JVaEntity;
import io.renren.zadmin.service.JMcardService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Service
public class JMcardServiceImpl extends CrudServiceImpl<JMcardDao, JMcardEntity, JMcardDTO> implements JMcardService {

    @Override
    public PageData<JMcardDTO> page(Map<String, Object> params) {
        IPage<JMcardEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JMcardDTO.class);
    }

    @Resource
    private SysDeptDao sysDeptDao;

    @Resource
    private JVaDao jVaDao;

    @Override
    public QueryWrapper<JMcardEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMcardEntity> wrapper = new QueryWrapper<>();

//        String agentId = (String) params.get("agentId");
//        if (StringUtils.isNotBlank(agentId)) {
//            wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", Long.parseLong(agentId));
//        }
//        String merchantId = (String) params.get("merchantId");
//        if (StringUtils.isNotBlank(merchantId)) {
//            wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", Long.parseLong(merchantId));
//        }
//        UserDetail user = SecurityUser.getUser();
//        if (agentId != null && ZestConstant.USER_TYPE_AGENT.equals(user.getUserType())) {
//            // 代理访问
//            wrapper.eq("agent_id", user.getDeptId());
//        } else if (merchantId == null && ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
//            // 商户访问
//            wrapper.eq("merchant_id", user.getDeptId());
//        }

        CommonFilter.setFilterMerchant(wrapper, params);

        return wrapper;
    }

    @Override
    public void save(JMcardDTO dto) {
        Long merchantId = dto.getMerchantId();
        SysDeptEntity merchantDept = sysDeptDao.selectById(merchantId);
        Long pid = merchantDept.getPid();
        SysDeptEntity agentDept = sysDeptDao.selectById(pid);
        dto.setAgentId(agentDept.getId());
        dto.setAgentName(agentDept.getName());
        dto.setMerchantName(merchantDept.getName());

        // 费用卡:  什么币种的卡， 就用那个va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(dto.getCurrency())).findFirst().get();
        dto.setPayerid(jVaEntity.getTid());

        super.save(dto);
    }

}