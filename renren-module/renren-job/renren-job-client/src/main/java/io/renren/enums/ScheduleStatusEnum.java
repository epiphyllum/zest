/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.enums;

/**
 * 定时任务状态
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.1.0
 */
public enum ScheduleStatusEnum {
    /**
     * 暂停
     */
    PAUSE(0),
    /**
     * 正常
     */
    NORMAL(1);

    private final int value;

    ScheduleStatusEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
