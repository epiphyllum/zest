package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.VCardDao;
import io.renren.zadmin.dto.VCardDTO;
import io.renren.zadmin.dto.VCardDTO;
import io.renren.zadmin.entity.VCardEntity;
import io.renren.zadmin.entity.VCardEntity;
import io.renren.zadmin.service.VCardService;
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
public class VCardServiceImpl extends CrudServiceImpl<VCardDao, VCardEntity, VCardDTO> implements VCardService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<VCardDTO> page(Map<String, Object> params) {
        IPage<VCardEntity> page = baseDao.selectPage(
                getPage(params, "stat_date", false),
                applyFilter(params)
        );
        return getPageData(page, VCardDTO.class);
    }

    @Override
    public QueryWrapper<VCardEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<VCardEntity> wrapper = new QueryWrapper<>();

        String statDate = (String)params.get("statDate");
        wrapper.eq(StringUtils.isNotBlank(statDate), "stat_date", statDate);
        String currency = (String)params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);
        String marketproduct = (String)params.get("marketproduct");
        wrapper.eq(StringUtils.isNotBlank(marketproduct), "marketproduct", marketproduct);

        commonFilter.setFilterAll(wrapper, params);
        return wrapper;
    }


}