package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JAllocateDao;
import io.renren.zadmin.dto.JAllocateDTO;
import io.renren.zadmin.dto.JAllocateDTO;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.service.JAllocateService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_inout
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@Service
public class JAllocateServiceImpl extends CrudServiceImpl<JAllocateDao, JAllocateEntity, JAllocateDTO> implements JAllocateService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JAllocateDTO> page(Map<String, Object> params) {
        IPage<JAllocateEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JAllocateDTO.class);
    }

    @Override
    public QueryWrapper<JAllocateEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JAllocateEntity> wrapper = new QueryWrapper<>();

        commonFilter.setFilterAll(wrapper, params);

        String type = (String) params.get("type");
        if (StringUtils.isNotBlank(type)) {
            wrapper.eq(StringUtils.isNotBlank(type), "type", type);
        }

        return wrapper;
    }

}