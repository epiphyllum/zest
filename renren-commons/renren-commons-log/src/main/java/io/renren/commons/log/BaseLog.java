/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.commons.log;

import lombok.Data;

import java.io.Serializable;

/**
 * Log基类
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Data
public abstract class BaseLog implements Serializable {
    /**
     * 日志类型
     */
    private Integer type;

}