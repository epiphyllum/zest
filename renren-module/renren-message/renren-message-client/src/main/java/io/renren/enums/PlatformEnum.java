/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.enums;

/**
 * 平台枚举
 *
 * @author Mark sunlightcs@gmail.com
 */
public enum PlatformEnum {
    /**
     * 阿里云
     */
    ALIYUN(1),
    /**
     * 腾讯云
     */
    QCLOUD(2),
    /**
     * 七牛云
     */
    QINIU(3);

    private final int value;

    PlatformEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}