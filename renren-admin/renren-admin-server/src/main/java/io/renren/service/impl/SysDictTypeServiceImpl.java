/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.BaseServiceImpl;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDictDataDao;
import io.renren.dao.SysDictTypeDao;
import io.renren.dto.SysDictTypeDTO;
import io.renren.entity.DictData;
import io.renren.entity.DictType;
import io.renren.entity.SysDictTypeEntity;
import io.renren.service.SysDictTypeService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 字典类型
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictTypeDao, SysDictTypeEntity> implements SysDictTypeService {
    @Resource
    private SysDictDataDao sysDictDataDao;

    @Override
    public PageData<SysDictTypeDTO> page(Map<String, Object> params) {
        IPage<SysDictTypeEntity> page = baseDao.selectPage(
                getPage(params, "sort", true),
                getWrapper(params)
        );

        return getPageData(page, SysDictTypeDTO.class);
    }

    private QueryWrapper<SysDictTypeEntity> getWrapper(Map<String, Object> params) {
        String dictType = (String) params.get("dictType");
        String dictName = (String) params.get("dictName");

        QueryWrapper<SysDictTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(dictType), "dict_type", dictType);
        wrapper.like(StringUtils.isNotBlank(dictName), "dict_name", dictName);

        return wrapper;
    }

    @Override
    public SysDictTypeDTO get(Long id) {
        SysDictTypeEntity entity = baseDao.selectById(id);

        return ConvertUtils.sourceToTarget(entity, SysDictTypeDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysDictTypeDTO dto) {
        SysDictTypeEntity entity = ConvertUtils.sourceToTarget(dto, SysDictTypeEntity.class);

        insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictTypeDTO dto) {
        SysDictTypeEntity entity = ConvertUtils.sourceToTarget(dto, SysDictTypeEntity.class);

        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //删除
        deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public List<DictType> getAllList() {
        List<DictType> typeList = baseDao.getDictTypeList();
        List<DictData> dataList = sysDictDataDao.getDictDataList();
        for (DictType type : typeList) {
            for (DictData data : dataList) {
                if (type.getId().equals(data.getDictTypeId())) {
                    type.getDataList().add(data);
                }
            }
        }
        return typeList;
    }

    @Override
    public List<DictType> getDictTypeList() {
        return baseDao.getDictTypeList();
    }

}