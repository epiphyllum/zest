/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.BaseServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysLogOperationDao;
import io.renren.dto.SysLogOperationDTO;
import io.renren.entity.SysLogOperationEntity;
import io.renren.service.SysLogOperationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 操作日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
public class SysLogOperationServiceImpl extends BaseServiceImpl<SysLogOperationDao, SysLogOperationEntity> implements SysLogOperationService {

    @Override
    public PageData<SysLogOperationDTO> page(Map<String, Object> params) {
        IPage<SysLogOperationEntity> page = baseDao.selectPage(
            getPage(params, Constant.CREATE_DATE, false),
            getWrapper(params)
        );

        return getPageData(page, SysLogOperationDTO.class);
    }

    @Override
    public List<SysLogOperationDTO> list(Map<String, Object> params) {
        List<SysLogOperationEntity> entityList = baseDao.selectList(getWrapper(params));

        return ConvertUtils.sourceToTarget(entityList, SysLogOperationDTO.class);
    }

    private QueryWrapper<SysLogOperationEntity> getWrapper(Map<String, Object> params){
        String module = (String) params.get("module");
        String status = (String) params.get("status");

        QueryWrapper<SysLogOperationEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(module), "module", module);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);

        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysLogOperationEntity entity) {
        insert(entity);
    }

}