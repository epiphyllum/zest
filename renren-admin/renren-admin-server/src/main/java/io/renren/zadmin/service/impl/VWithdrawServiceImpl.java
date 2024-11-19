package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.VWithdrawDao;
import io.renren.zadmin.dto.VWithdrawDTO;
import io.renren.zadmin.dto.VWithdrawDTO;
import io.renren.zadmin.entity.VWithdrawEntity;
import io.renren.zadmin.entity.VWithdrawEntity;
import io.renren.zadmin.service.VWithdrawService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * VIEW
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@Service
public class VWithdrawServiceImpl extends CrudServiceImpl<VWithdrawDao, VWithdrawEntity, VWithdrawDTO> implements VWithdrawService {

    @Override
    public PageData<VWithdrawDTO> page(Map<String, Object> params) {
        IPage<VWithdrawEntity> page = baseDao.selectPage(
                getPage(params, "stat_date", false),
                applyFilter(params)
        );
        return getPageData(page, VWithdrawDTO.class);
    }

    @Override
    public QueryWrapper<VWithdrawEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<VWithdrawEntity> wrapper = new QueryWrapper<>();

        String agentId = (String)params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);
        String merchantId = (String)params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);
        String subId = (String)params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);
        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);
        String marketproduct = (String)params.get("marketproduct");
        wrapper.eq(StringUtils.isNotBlank(marketproduct), "marketproduct", marketproduct);
        String statDate = (String)params.get("statDate");
        wrapper.eq(StringUtils.isNotBlank(statDate), "stat_date", statDate);

        return wrapper;
    }


}