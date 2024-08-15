/**
 * Copyright (c) 2021 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysPostDao;
import io.renren.dto.SysPostDTO;
import io.renren.entity.SysPostEntity;
import io.renren.service.SysPostService;
import io.renren.service.SysUserPostService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 岗位管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysPostServiceImpl extends CrudServiceImpl<SysPostDao, SysPostEntity, SysPostDTO> implements SysPostService {
    @Resource
    private SysUserPostService sysUserPostService;

    @Override
    public QueryWrapper<SysPostEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<SysPostEntity> wrapper = new QueryWrapper<>();

        String postCode = (String) params.get("postCode");
        wrapper.like(StringUtils.isNotBlank(postCode), "post_code", postCode);

        String postName = (String) params.get("postName");
        wrapper.like(StringUtils.isNotBlank(postName), "post_name", postName);

        String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq("status", Integer.parseInt(status));
        }

        wrapper.orderByAsc("sort");

        return wrapper;
    }

    @Override
    public List<SysPostDTO> list(Map<String, Object> params) {
        List<SysPostEntity> entityList = baseDao.selectList(getWrapper(params));

        return ConvertUtils.sourceToTarget(entityList, SysPostDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //删除岗位
        baseDao.deleteBatchIds(Arrays.asList(ids));

        //删除岗位用户关系
        sysUserPostService.deleteByPostIds(ids);
    }
}