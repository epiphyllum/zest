/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.commons.log.enums;

/**
 * 日志类型枚举
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public enum LogTypeEnum {
    /**
     * 登录日志
     */
    LOGIN(0),
    /**
     * 操作日志
     */
    OPERATION(1),
    /**
     * 异常日志
     */
    ERROR(2);

    private final int value;

    LogTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}