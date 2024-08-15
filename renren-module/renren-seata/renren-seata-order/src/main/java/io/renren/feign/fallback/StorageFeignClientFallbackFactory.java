/**
 * Copyright (c) 2016-2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.feign.fallback;

import io.renren.commons.tools.utils.Result;
import io.renren.feign.StorageFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 库存 Feign Client FallbackFactory
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@Component
public class StorageFeignClientFallbackFactory implements FallbackFactory<StorageFeignClient> {
    @Override
    public StorageFeignClient create(Throwable throwable) {
        log.error("{}", throwable);

        return (commodityCode, count) -> new Result().error();
    }
}
