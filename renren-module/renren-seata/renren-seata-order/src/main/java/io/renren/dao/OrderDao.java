/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 订单
*
* @author Mark sunlightcs@gmail.com
*/
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {
	
}