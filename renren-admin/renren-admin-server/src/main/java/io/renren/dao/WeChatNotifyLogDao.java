/**
 * Copyright (c) 2021 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.dao;


import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.WeChatNotifyLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信支付回调日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface WeChatNotifyLogDao extends BaseDao<WeChatNotifyLogEntity> {

}