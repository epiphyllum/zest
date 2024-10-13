package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dto.JExchangeDTO;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.service.JExchangeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_exchange
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JExchangeServiceImpl extends CrudServiceImpl<JExchangeDao, JExchangeEntity, JExchangeDTO> implements JExchangeService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JExchangeDTO> page(Map<String, Object> params) {
        IPage<JExchangeEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JExchangeDTO.class);
    }

    @Override
    public QueryWrapper<JExchangeEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JExchangeEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterMerchant(wrapper, params);
        return wrapper;
    }

}