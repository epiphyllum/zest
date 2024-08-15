/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.redis;

import io.renren.commons.tools.redis.RedisKeys;
import io.renren.commons.tools.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 参数管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Component
public class SysParamsRedis {
    @Resource
    private RedisUtils redisUtils;

    public void delete(Object[] paramCodes) {
        String key = RedisKeys.getSysParamsKey();
        redisUtils.hDel(key, paramCodes);
    }

    public void set(String paramCode, String paramValue) {
        if (paramValue == null) {
            return;
        }
        String key = RedisKeys.getSysParamsKey();
        redisUtils.hSet(key, paramCode, paramValue);
    }

    public String get(String paramCode) {
        String key = RedisKeys.getSysParamsKey();
        return (String) redisUtils.hGet(key, paramCode);
    }

}
