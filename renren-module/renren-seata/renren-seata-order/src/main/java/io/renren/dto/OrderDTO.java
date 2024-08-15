/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@Schema(description = "订单")
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;
    @Schema(description = "商品编码")
    private String commodityCode;
    @Schema(description = "数量")
    private Integer count;
    @Schema(description = "金额")
    private BigDecimal money;

}