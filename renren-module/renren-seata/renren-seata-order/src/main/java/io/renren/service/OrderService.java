/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.dto.OrderDTO;
import io.renren.entity.OrderEntity;

/**
 * 订单
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {

    /**
     * 创建订单、减库存，涉及到两个服务
     * @param commodityCode   商品编码
     * @param count           数量
     */
    void createOrder(String commodityCode, Integer count);
}