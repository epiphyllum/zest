package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.service.impl.SysDeptServiceImpl;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dto.JBalanceDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.service.JBalanceService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * j_balance
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Service
public class JBalanceServiceImpl extends CrudServiceImpl<JBalanceDao, JBalanceEntity, JBalanceDTO> implements JBalanceService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JBalanceDTO> page(Map<String, Object> params) {
        IPage<JBalanceEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JBalanceDTO.class);
    }

    @Override
    public List<JBalanceDTO> list(Map<String, Object> params) {
        List<JBalanceEntity> jBalanceEntities = baseDao.selectList(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return ConvertUtils.sourceToTarget(jBalanceEntities, JBalanceDTO.class);
    }

    @Override
    public QueryWrapper<JBalanceEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JBalanceEntity> wrapper = new QueryWrapper<>();

        // ownerId优先,  如果有， 就只看ownerId的
        commonFilter.setLogBalanceFilter(wrapper, params);

        String ownerType = (String) params.get("ownerType");
        wrapper.eq(StringUtils.isNotBlank(ownerType), "owner_type", ownerType);

        String balanceType = (String) params.get("balanceType");
        wrapper.likeRight(StringUtils.isNotBlank(balanceType), "balance_type", balanceType);

        String currency = (String) params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);
        return wrapper;
    }

}