/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.enums;

/**
 * 菜单类型枚举
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public enum MenuTypeEnum {
    /**
     * 菜单
     */
    MENU(0),
    /**
     * 按钮
     */
    BUTTON(1);

    private final int value;

    MenuTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
