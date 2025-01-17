/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service;

import io.renren.commons.mybatis.service.BaseService;
import io.renren.commons.tools.page.PageData;
import io.renren.entity.FieldTypeEntity;

import java.util.Map;
import java.util.Set;

/**
 * 字段类型管理
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface FieldTypeService extends BaseService<FieldTypeEntity> {
    PageData<FieldTypeEntity> page(Map<String, Object> params);

    Map<String, FieldTypeEntity> getMap();

    /**
     * 根据tableId，获取包列表
     */
    Set<String> getPackageListByTableId(Long tableId);

    Set<String> list();
}