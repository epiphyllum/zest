/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.commons.mybatis.enums;

/**
 * 删除标识枚举类
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public enum DelFlagEnum {
    NORMAL(0),
    DEL(1);

    private int value;

    DelFlagEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
