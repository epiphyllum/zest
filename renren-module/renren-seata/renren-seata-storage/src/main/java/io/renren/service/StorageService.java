/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.dto.StorageDTO;
import io.renren.entity.StorageEntity;

/**
 * 库存表
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface StorageService extends CrudService<StorageEntity, StorageDTO> {

    /**
     * 减库存
     *
     * @param commodityCode 商品代码
     * @param count         数量
     */
    void deduct(String commodityCode, int count);

}