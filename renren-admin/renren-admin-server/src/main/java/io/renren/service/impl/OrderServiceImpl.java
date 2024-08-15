/**
 * Copyright (c) 2021 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.dao.OrderDao;
import io.renren.dto.OrderDTO;
import io.renren.entity.OrderEntity;
import io.renren.enums.OrderStatusEnum;
import io.renren.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 订单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

    @Override
    public QueryWrapper<OrderEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();

        String orderId = (String)params.get("orderId");
        wrapper.eq(StringUtils.isNotBlank(orderId), "order_id", orderId);

        String status = (String)params.get("status");
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);

        String userId = (String)params.get("userId");
        wrapper.eq(StringUtils.isNotBlank(userId), "user_id", userId);

        wrapper.orderByDesc("create_date");

        return wrapper;
    }

    @Override
    public void save(OrderDTO dto) {
        dto.setOrderId(IdWorker.getId());
        dto.setUserId(SecurityUser.getUserId());
        dto.setStatus(OrderStatusEnum.WAITING.getValue());
        super.save(dto);
    }

    @Override
    public OrderEntity getByOrderId(Long orderId) {
        return baseDao.getByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(OrderEntity order) {
        baseDao.paySuccess(order.getOrderId(), OrderStatusEnum.FINISH.getValue(), new Date());
    }
}