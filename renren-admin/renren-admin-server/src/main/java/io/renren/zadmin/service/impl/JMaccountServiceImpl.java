package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dto.JMaccountDTO;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.service.JMaccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_maccount
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JMaccountServiceImpl extends CrudServiceImpl<JMaccountDao, JMaccountEntity, JMaccountDTO> implements JMaccountService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JMaccountDTO> page(Map<String, Object> params) {
        IPage<JMaccountEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JMaccountDTO.class);
    }

    @Override
    public QueryWrapper<JMaccountEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JMaccountEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterMerchant(wrapper, params);
        return wrapper;
    }
}