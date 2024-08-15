/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.UReportDataEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报表管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface UReportDataDao extends BaseDao<UReportDataEntity> {
}
