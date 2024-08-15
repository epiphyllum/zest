/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.commons.security.enums;

/**
 * 用户状态
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public enum UserStatusEnum {
    DISABLE(0),
    ENABLED(1);

    private final int value;

    UserStatusEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
