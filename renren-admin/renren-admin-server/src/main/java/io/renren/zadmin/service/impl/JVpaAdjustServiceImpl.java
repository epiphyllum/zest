package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JVpaAdjustDao;
import io.renren.zadmin.dto.JVpaAdjustDTO;
import io.renren.zadmin.dto.JVpaAdjustDTO;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zadmin.service.JVpaAdjustService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_vpa_adjust
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-02
 */
@Service
public class JVpaAdjustServiceImpl extends CrudServiceImpl<JVpaAdjustDao, JVpaAdjustEntity, JVpaAdjustDTO> implements JVpaAdjustService {


    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JVpaAdjustDTO> page(Map<String, Object> params) {
        IPage<JVpaAdjustEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JVpaAdjustDTO.class);
    }

    @Override
    public QueryWrapper<JVpaAdjustEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JVpaAdjustEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        String agentId = (String) params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);

        String merchantId = (String) params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);

        String subId = (String) params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);

        String cardno = (String) params.get("cardno");
        wrapper.eq(StringUtils.isNotBlank(cardno), "cardno", cardno);

        String marketproduct = (String) params.get("marketproduct");
        wrapper.eq(StringUtils.isNotBlank(marketproduct), "marketproduct", marketproduct);

        return wrapper;
    }


}