package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JB2bDao;
import io.renren.zadmin.dto.JB2bDTO;
import io.renren.zadmin.dto.JMoneyDTO;
import io.renren.zadmin.entity.JB2bEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zadmin.service.JB2bService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_b2b
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2025-04-23
 */
@Service
public class JB2bServiceImpl extends CrudServiceImpl<JB2bDao, JB2bEntity, JB2bDTO> implements JB2bService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JB2bDTO> page(Map<String, Object> params) {
        IPage<JB2bEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JB2bDTO.class);
    }

    @Override
    public QueryWrapper<JB2bEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JB2bEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterMerchant(wrapper, params);
        return wrapper;
    }

}