/**
 * Copyright (c) 2021 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.SysPostEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 岗位管理
*
* @author Mark sunlightcs@gmail.com
*/
@Mapper
public interface SysPostDao extends BaseDao<SysPostEntity> {
	
}