/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.enums;

/**
 * 菜单资源标识
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public enum MenuFlagEnum {
    /**
     * 菜单资源
     */
    YES(1),
    /**
     * 非菜单资源
     */
    NO(0);

    private final int value;

    MenuFlagEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}