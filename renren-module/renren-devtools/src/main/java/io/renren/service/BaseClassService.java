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
import io.renren.entity.BaseClassEntity;

import java.util.List;
import java.util.Map;

/**
 * 基类管理
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface BaseClassService extends BaseService<BaseClassEntity> {

    PageData<BaseClassEntity> page(Map<String, Object> params);

    List<BaseClassEntity> list();
}