/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 库存表
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("seata_storage")
public class StorageEntity {
	private static final long serialVersionUID = 1L;

	/**
	* id
	*/
	private Long id;
	/**
	* 商品编码
	*/
	private String commodityCode;
	/**
	* 商品库存数
	*/
	private Integer total;
}