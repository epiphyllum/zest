package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.entity.JVaEntity;
import io.renren.zadmin.service.JMcardService;
import jakarta.annotation.Resource;
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
    @Resource
    private CommonFilter commonFilter;

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
        commonFilter.setFilterMerchant(wrapper, params);
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