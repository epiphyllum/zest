/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service;

import io.renren.commons.dynamic.datasource.annotation.DataSource;
import io.renren.commons.mybatis.service.impl.BaseServiceImpl;
import io.renren.dao.SysUserDao;
import io.renren.entity.SysUserEntity;

/**
 * 测试多数据源
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.1.0
 */
@DataSource("slave2")
public class DynamicService extends BaseServiceImpl<SysUserDao, SysUserEntity> {
}
