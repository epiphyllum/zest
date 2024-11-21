package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.VDepositDao;
import io.renren.zadmin.dto.VDepositDTO;
import io.renren.zadmin.dto.VDepositDTO;
import io.renren.zadmin.entity.VDepositEntity;
import io.renren.zadmin.entity.VDepositEntity;
import io.renren.zadmin.service.VDepositService;
import jakarta.annotation.Resource;
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
public class VDepositServiceImpl extends CrudServiceImpl<VDepositDao, VDepositEntity, VDepositDTO> implements VDepositService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<VDepositDTO> page(Map<String, Object> params) {
        IPage<VDepositEntity> page = baseDao.selectPage(
                getPage(params, "stat_date", false),
                applyFilter(params)
        );
        return getPageData(page, VDepositDTO.class);
    }

    @Override
    public QueryWrapper<VDepositEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<VDepositEntity> wrapper = new QueryWrapper<>();

        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);

        String marketproduct = (String)params.get("marketproduct");
        wrapper.eq(StringUtils.isNotBlank(marketproduct), "marketproduct", marketproduct);

        String statDate = (String)params.get("statDate");
        wrapper.eq(StringUtils.isNotBlank(statDate), "stat_date", statDate);

        commonFilter.setFilterAll(wrapper, params);

        return wrapper;
    }


}